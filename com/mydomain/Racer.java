package com.mydomain;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

/**
 * IEOR 140 Team 6
 * Authors: Moonsoo Choi, Sherman Siu
 * Date: October 4, 2012
 * Class Name: Racer
 * Class Description: Racer controls the navigation of the robot as it shuffles between beacon lights.
 * The robot will steer towards the light, and will call on the Avoider class when Detector detects an
 * obstacle along the way.
 */
public class Racer 
{	
	// Declaring instance variables.
	Avoider myAvoid = new Avoider();
	public DifferentialPilot myPilot;
	int _speed;
	int _acceleration;
	int _rotatespeed;
	public Scanner mySR;
	public Detector myDetector;
	
	/**
	 * Racer class will be called upon when one provides the fields for DifferentialPilot, Scanner,
	 * and Detector.
	 * @param Pilot
	 * @param SR
	 * @param _Detector
	 */
	public Racer(DifferentialPilot Pilot, Scanner SR, Detector _Detector)
	{
		myPilot = Pilot;
		mySR = SR;
		myDetector = _Detector;
	}
	
	/**
	 * Sets the speed of travel speed of the robot.
	 * @param speed
	 */
	public void Speed(int speed)
	{
		_speed = speed;
		myPilot.setTravelSpeed(_speed);
	}
	
	/**
	 * Sets the speed of rotation speed of the robot.
	 * @param rotatespeed
	 */
	public void RotateSpeed(int rotatespeed)
	{
		_rotatespeed = rotatespeed;
		myPilot.setRotateSpeed(_rotatespeed);
	}
	
	/**
	 * Sets the acceleration of the robot.
	 * @param acceleration
	 */
	public void Acceleration(int acceleration)
	{
		_acceleration = acceleration;
		myPilot.setAcceleration(_acceleration);
	}
	
	/**
	 * Navigates the robot as it shuffles between two beacon lights. The robot will steer towards 
	 * the light. Uses the Scanner class to determine the angle with the maximum light intensity.
	 * With the angle acquired, robot will steer at that angle rate*gain. If an obstacle is detected 
	 * by the Detector class, robot will temporarily halt its steering towards the light and call 
	 * upon the Avoider class to react to the obstacle. The robot will constantly be checking if there
	 * are obstacles nearby when it rotates to ensure that it won't run into an obstacle trap.  
	 * When the robot reaches the light, the robot will travel backwards for 15 inches, 
	 * then makes a turn of about 180 degrees. The method will then terminate. 
	 * @param Light
	 */
	public void gotoLight(int Light) 
	{
		// best angle represents the amount of angle where light intensity value is the greatest
		int bestAngle = mySR.getTargetBearing();
		myPilot.setTravelSpeed(_speed); // setting the traveling speed of a robot
		mySR.setSpeed(999);  // setting the rotating speed of the scanner
		float gain = 0.28f; // a variable that can help us determine an appropriate steering amount
		mySR.rotateTo(bestAngle, true); //Rotate the scanner motor to the bestAngle measured.
		mySR.scanTo(80); // scanning to the left 80 degrees
		mySR.scanTo(-80); // scanning to the right 80 degree
		
		/* 
		 * While the current light intensity value is less than its minimum,
		 * make the robot steer with the angle that produces the most amount of light 
		 * intensity we have so far. If an obstacle is detected, call upon the Avoider class to rotate, 
		 * then re-scan and determine if any obstacles are nearby. If not, then robot will travel until
		 * it detects an obstacle, and then rotate back towards its original direction. The robot will
		 * then continue towards the light until it reaches the light, at which point it backs and rotates,
		 * or detects another obstacle, in which during that case it avoids again.
		 */
		while (mySR.getLight() < Light)
		{
				//Steer robot using the bestAngle acquired earlier.
				myPilot.steer(gain*bestAngle);	
				/*
				 * If an obstacle is detected, call upon the Avoider class to rotate, then 
				 * re-scan and determine if any obstacles are nearby. If not, the robot will then 
				 * travel until it detects an obstacle and then it will return back to its original 
				 * direction, re-scan for the light, and begin steering towards the light again.
				 */
				while (myDetector._detected)
				{
					if (mySR.getLight()<Light-6) //if the robot has not approached the light yet
					{
						//Call on Avoider.java Avoid method, which turns the robot either left or right.
						myAvoid.Avoid(bestAngle, myDetector._crashed,myDetector._isObstacleLeft,myDetector._isObstacleRight,myDetector._angle); //Call upon Avoider class to avoid.
						//Reset all the boolean variables.
						myDetector._crashed = false;
						myDetector._isObstacleLeft = false;
						myDetector._isObstacleRight = false;
						myPilot.stop(); //Stop the robot.			
						mySR.scanTo(30); //Scan to the left 30 degrees.
						mySR.scanTo(-30); //Scan to the right 30 degrees.
						mySR.rotateTo(0, true); //Rotate the scanner to 0 (straight ahead)
					
						//If the robot crashes or detects an obstacles either left or right
						if(!myDetector._isObstacleLeft||!myDetector._isObstacleRight||!myDetector._crashed)
						{	
							/*
							 * Robot goes forward 28 inches, but it will be stop if an obstacle is 
							 * detected or the robot crashes during this movement. In that case the robot
							 * will stop and rotate back to its original direction. 
							 */
							myPilot.travel(28,true); 
							while(myPilot.isMoving())
							{
								//While robot is moving, if an obstacle is detected, stop the robot.
								if (myDetector._isObstacleLeft||myDetector._isObstacleRight||myDetector._crashed)
								{
									myPilot.stop();
								}
							}
							myAvoid.Rotate(); //Robot rotates back to original direction.
							mySR.scanTo(35); // scanning to the left 35 degrees
							mySR.scanTo(-35); // scanning to the right 35 degrees							
							/*
							 * If there are no obstacles detected or the robot didn't crash, set 
							 * a new bestAngle, and set myDetector._detected to false.
							 */
							if (!myDetector._isObstacleLeft||!myDetector._isObstacleRight||!myDetector._crashed)
							{	
								bestAngle = mySR.getTargetBearing();
								myDetector._detected = false;
							}
						}
					}
					/*
					 * If the robot is actually at the light, just set all detector booleans to false
					 * and treat this as a false alarm.
					 */
					else
					{
						myDetector._crashed = false;
						myDetector._isObstacleLeft = false;
						myDetector._isObstacleRight = false;
						myDetector._detected = false;
					}
				}
				/*
				 * After the whole detection and avoiding scheme, make sure that the robot is facing the
				 * right direction and not re-tracking the light it came from. We accomplish this by 
				 * taking the remainder of bestAngle/90. If the result is a very large or very small 
				 * value, then the robot will rotate either left or right to get back on track.
				 */
				if(bestAngle%90 > 87)
				{
					myPilot.rotate(80); //Rotate left
				}
				else if(bestAngle%90 < -87)
				{
					myPilot.rotate(-80); //Rotate right
				}
				
				// continuously scan for the light, scanning with range of +/-45 degrees of current angle			
				mySR.scanTo(Math.min(bestAngle+45,75));
				bestAngle = mySR.getTargetBearing();	
				mySR.scanTo(Math.max(bestAngle-45,-75));
				bestAngle = mySR.getTargetBearing();
		}
		//When the robot reaches the light, travel back 15 inches, rotate about 180 degrees.
		myPilot.travel(-15); //Travel back 15 inches
		myPilot.stop(); //Robot stops.
		/*
		 * Robot rotates about 180 degrees - getAngleIncrement(), the net angle changed since the start.
		 */
		myPilot.rotate(180-myPilot.getAngleIncrement()); 
		Delay.msDelay(100); //100 millisecond delay.
	}
}

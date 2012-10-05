package com.mydomain;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

/**
 * IEOR 140 Team 6
 * Authors: Sherman Siu, Moonsoo Choi
 * Class Name: Avoider
 * Class Description: The Avoider class only comes into affect when the robot either detects a nearby
 * object via its ultrasonic sensor or its touch sensors are touched (see Detector.java). The method
 * Avoid() will stop the robot and rotate the robot either left or right depending on which booleans
 * in the Detector class were triggered to become true. In the event a touch sensor is activated, then
 * the robot will back off 17 inches before rotating. The rotate() method returns the robot back to its
 * original direction once it avoids the obstacle. TotalAngle tracks the net angle the robot has turned
 * to avoid the obstacle; we use it because we need to know how to return the robot to its original direction
 * after the robot avoids the obstacle(s).
 * @author Moonsoo Choi, Sherman Siu
 *
 */
public class Avoider 
{
	//Instance variables set
	DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),5.5f,Motor.A,Motor.C,false);
	Detector myDetector;
	Racer myRacer;
	int TotalAngle = 0;
	
	/**
	 * returns the total angle, the net angle the robot turns when it is avoiding obstacles.
	 * @return Total Angle
	 */
	public int TotalAngle()
	{
		return TotalAngle;
	}
	
	/**
	 * The Avoid() method takes the booleans acquired from Racer and Detector as well as the angle and
	 * bestAngles to determine what happened in the Detector. If the robot crashed, the robot will back
	 * off 17 inches. If the robot either crashed from the left side or the ultrasonic sensor detected an
	 * obstacle from the left side, the robot will rotate right 88 degrees. If the robot crashed from the 
	 * right side or the ultrasonic sensor detected an obstacle from the right side, the robot will rotate
	 * left 88 degrees. If the robot crashes in the middle, thus triggering both left and right sensor,
	 * the robot will then use the bestAngle from Racer to turn left or right, depending on whether the
	 * light is currently located left or right of the robot. Meanwhile, TotalAngle is tracked to determine 
	 * how much exactly the robot has turned while avoiding and when the robot is done avoiding, we 
	 * know how much and which direction the robot needs to turn back to get to its original direction. 
	 * @param bestAngle
	 * @param _crashed
	 * @param _isObstacleLeft
	 * @param _isObstacleRight
	 * @param angle
	 */
	public void Avoid(int bestAngle, boolean _crashed,boolean _isObstacleLeft,boolean _isObstacleRight, int angle)
	{
		myPilot.stop(); //stops the robot
		//if robot crashes
		if (_crashed) 
		{
			myPilot.travel(-17); //robot travels backwards 17 inches
		}
		//if robot crashed from the right side or detected an obstacle from the right side
		if (_isObstacleRight && !_isObstacleLeft)
		{
			myPilot.rotate(88); //rotate robot left 88 degrees
			TotalAngle = TotalAngle + 88; //add 88 angles to the TotalAngle
		}
		//else if the robot crashed from the left side or detected an obstacle from the left side
		else if (_isObstacleLeft && !_isObstacleRight)
		{
			myPilot.rotate(-88); //rotate robot right 88 degrees
			TotalAngle = TotalAngle - 88; //subtract 88 degrees from TotalAngle
		}
		//else if the robot crashed via the middle (trigger both left and right booleans to be true)
		else if(_isObstacleLeft && _isObstacleRight)
		{
			/*
			 * take bestAngle (direction angle of the light). If bestAngle is greater than or equal to
			 * zero (light is left of robot), rotate left 88 degrees, add 88 degrees to TotalAngle.
			 */
			if(bestAngle >= 0)
			{
				myPilot.rotate(88);
				TotalAngle = TotalAngle + 88;
			}
			/*
			 * Else if bestAngle is less than 0 degrees (light is right of robot), the robot will rotate
			 * 88 degrees to the right and we subtract 88 degrees from the TotalAngles 
			 */
			else if(bestAngle < 0)
			{
				myPilot.rotate(-88);
				TotalAngle = TotalAngle - 88;
			}
		}
	}
	
	/*
	 * The Rotate() method returns the robot back to its original direction after avoiding obstacle(s).
	 * When the robot was avoiding, TotalAngle tracked the net angle the robot turned (so if the robot
	 * turned left, right, left, then the robot will be net left). In this method, the robot will rotate
	 * the opposite of the TotalAngle acquired (so if the robot was net left, then the robot will need to
	 * turn right to get back on track). Once turned, reset TotalAngle back to zero.
	 */
	public void Rotate()
	{
		myPilot.rotate(-TotalAngle); //rotate opposite of TotalAngle
		TotalAngle = 0; //reset TotalAngle
	}
}


package com.mydomain;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

/**
 * IEOR 140 Team 6
 * Authors: Moonsoo Choi, Sherman Siu
 * Date: October, 2012
 * Class Name: Milestone3
 * Class Description: Robot completes laps between the beacon lights, referring to
 * the Racer class as a means of navigating the robot towards the light and through
 * certain obstacles.
 *  * @author Moonsoo Choi, Sherman Siu
 */
public class Milestone3 
{
	/**
	 * The main method contains three main parts: setting up the constructors, setting robot speeds,
	 * and then commanding the robot to complete 8 laps. 
	 * The following instance variables are set: DifferentialPilot, LightSensor, UltrasonicSensor, 
	 * TouchSensors left and right, and Racer. The UltrasonicSensor connects to the Detector class, 
	 * the LightSensor connects to the Scanner class, and the Racer connects to the Racer class.
	 * The acceleration, rotating speed, and traveling speed are turned on; detector thread is started.
	 * Robot then completes  laps, using GotoLight() method to navigate.
	 *  @param args
	 */
	public static void main(String[] args) 
	{
		/*
		 * Instance variables set: DifferentialPilot, LightSensor, UltrasonicSensor, TouchSensors, Scanner,
		 * Detector, and Racer.
		 */
		DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),5.5f,Motor.A,Motor.C,false);
		LightSensor scanner = new LightSensor(SensorPort.S2);
		UltrasonicSensor UltraSonic = new UltrasonicSensor(SensorPort.S3);
		TouchSensor TouchLeft = new TouchSensor(SensorPort.S1);
		TouchSensor TouchRight = new TouchSensor(SensorPort.S4);
		Scanner mySR = new Scanner(Motor.B, scanner);
		Detector myDetector = new Detector(Motor.B,UltraSonic,TouchLeft,TouchRight);
		Racer myRacer = new Racer(myPilot,mySR,myDetector);

		myDetector.start(); //Start the Detector thread.
		myRacer.Speed(12); //Set robot travel speed at 12 in/sec.
		myRacer.Acceleration(15); //set acceleration of robot at 15 in/sec/sec.
		myRacer.RotateSpeed(720); //set rotation speed of robot in degree/sec.
		
		//The robot will run the while loop forever (until it is manually stopped).
		while (true)
		{
			/*
			 * Robot steers using myRacer till it finds light intensity of 52. Robot will then travel
			 * backwards for 15 in, rotate about 180 degrees, and repeat the steering. See java code
			 * Racer.java for more info.
			 */
			myRacer.gotoLight(52); 
		}
	}

}


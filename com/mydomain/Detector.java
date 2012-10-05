package com.mydomain;

import lejos.nxt.*;
import lejos.util.Datalogger;
/**
 * IEOR 140 Team 6
 * Authors: Sherman Siu, Moonsoo Choi
 * Date: October 4, 2012
 * Class Name: Detector
 * Class Description: Detects if there are any obstacles within a range of 2-3 feet of the robot's 
 * Ultrasonic Sensor. Also detects if the touch sensors were used by the robot.
 * @author Moonsoo Choi, Sherman Siu
 */
class Detector extends Thread
{
	/**
	 * The constructor creates Detector and calls upon the fields Motor, Ultrasonic Sensor, TouchSensors
	 * left and right.  
	 * @param theMotor
	 * @param ear
	 * @param lefthand
	 * @param righthand
	 */
   public Detector(NXTRegulatedMotor theMotor, UltrasonicSensor ear, TouchSensor lefthand, TouchSensor righthand)
   {
      motor = theMotor;    
      _ear = ear;
      _leftHand = lefthand;
      _rightHand = righthand;
   }

   /**
    * returns the angle at which the maximum light intensity was found
    * @return TargetBearing
    */
   public int getTargetBearing()
   {
      return getTargetBearing();
   }

   /**
    * returns the angle in which the light sensor is pointing
    * @return the angle
    */
   public int getHeadAngle()
   {
      return motor.getTachoCount();
   }
   
   /**
    * returns the distance of the obstacle in which the ultrasonic sensor is pointing
    * @return Obstacle distance
    */
   public int getDist()
   {
	   return _ear.getDistance();
   }
   
   /**
    * sets the motor speed in deg/sec
    * @param speed 
    */
   public void setSpeed(int speed)
   {
      motor.setSpeed(speed);
   }
   
   /**
    * returns boolean, see whether an obstacle is detected
    * @param _detected
    * @return _detected
    */
   public boolean isDetected(boolean _detected)
   {
	   return _detected;
   }
   
   /**
    * returns boolean, to see if the obstacle detected is from the left side
    * @param _isObstacleLeft
    * @return _isObstacleLeft
    */
   public boolean isObstacleLeft(boolean _isObstacleLeft)
   {
	   return _isObstacleLeft;
   }
   
   /**
    * returns boolean, to see if the obstacle detected is from the right side
    * @param _isObstacleRight
    * @return _isObstacleRight
    */
   public boolean isObstacleRight(boolean _isObstacleRight)
   {
	   return _isObstacleRight;
   }
   
   /**
    * returns boolean, to see if the obstacle detected is via crashing/touch sensors
    * @param _crashed
    * @return _crashed
    */
   public boolean isCrashed(boolean _crashed)
   {
	   return _crashed;
   }
   
   /**
    * The run() method tracks the distance between an object and the robot and the angle at which the object
    * is detected. If the distance between the obstacle and the robot is less than 3 feet or the left/right
    * touch sensors are pressed, then turn boolean _detected=false to _detected=true. Depending on
    * the condition of detection (crash, from left/right), other booleans will turn true as well. 
    * This is constantly tracked via a while loop.
    */
   public void run()
   {
      int obstacle_distance; //initialize obstacle_distance, the distance between the robot and an object
      //set all booleans in the loop to be originally false
      _detected = false;
      _crashed = false;
      _isObstacleLeft = false;
      _isObstacleRight = false;
      
      //this loop will be constantly running
      while(true)
      {
    	  obstacle_distance = _ear.getDistance(); //obstacle_distance gets the distance b/w robot and object
    	  _angle = motor.getTachoCount(); //_angle retrieves the angle of the motor at detection time
    	  /*
    	   * If the left touch sensor is pressed, set booleans _crashed, _isObstacleLeft, and
    	   * _detected to be true
    	   */
    	  if (_leftHand.isPressed())
    	  {
    		  _crashed = true;
    		  _isObstacleLeft = true;
    		  _detected = true;
    	  }
    	  /*
    	   * Else if the right touch sensor is pressed, set booleans _crashed, _isObstacleRight, and 
    	   * _detected to be true
    	   */
    	  else if (_rightHand.isPressed())
    	  {
    		  _crashed = true;
    		  _isObstacleRight = true;
    		  _detected = true;  
    	  }
    	  /*
    	   * Else if the obstacle_distance detected is less than 50 and the absolute value of the angle 
    	   * of detection is less than 3 degrees, check to see the angle of detection. If the angle is 
    	   * positive (left), then trigger booleans _isObstacleLeft and _detected to be true. Else,
    	   * trigger booleans _isObstacleRight and _detected to be true.
    	   */
    	  else if (obstacle_distance < 50 && Math.abs(_angle) < 3)
    	  {
    		  if (_angle>0) //if _angle is a positive value
    		  {
    			  _isObstacleLeft = true; //trigger _isObstacleLeft to be true
    		  }
    		  else
    		  {
    			  _isObstacleRight = true; //else trigger _isObstacleRight to be true
    		  }
    		  _detected = true; //_detected will become true regardless of angle
    	  }
    	  /*
    	   * Else if both touch sensors are simultaneously pressed (crash from the middle), turn
    	   * all the booleans (_crashed, _isObstacleLeft, _isObstacleRight, _detected) to be true.
    	   */
    	  else if (_leftHand.isPressed()&&_rightHand.isPressed())
    	  {
    		  _crashed = true;
    		  _isObstacleRight = true;
    		  _isObstacleLeft = true;
    		  _detected = true;
    	  }
      }
   }

   /******* instance variabled ***************/
   NXTRegulatedMotor motor;
   UltrasonicSensor _ear;
   TouchSensor _leftHand;
   TouchSensor _rightHand;
   int _angle;
   boolean _detected = false;
   boolean _crashed = false;
   boolean _isObstacleLeft = false;
   boolean _isObstacleRight = false;
}

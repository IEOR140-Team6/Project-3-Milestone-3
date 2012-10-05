package com.mydomain;

import lejos.nxt.*;
import lejos.util.Datalogger;

/**
 * IEOR 140 Team 6
 * Members: Sherman Siu, Moonsoo Choi
 * Class Name: Scanner
 * Class Description: Scanner scans the area for the maximum light intensity. The value of the max
 * light intensity and the angle it is detected at are recorded.
 * @author Moonsoo Choi, Sherman Siu, part of code from Prof. Glassey
 */

public class Scanner
{
	/**
	 * Constructor calls for the fields NXTRegulatedMotor and LightSensor.
	 * @param theMotor
	 * @param eye
	 */
	public Scanner(NXTRegulatedMotor theMotor, LightSensor eye)
	{
		motor = theMotor;    
		_eye = eye;
		_eye.setFloodlight(false);
	}

	/**
	 * rotate the scanner head to the angle
	 * @param angle
	 * @param instantReturn if true, the method is non-blocking
	 */
	public void rotateTo(int angle, boolean instantReturn)
	{
		motor.rotateTo(angle, instantReturn);
	}

   /**
    * returns the angle at which the maximum light intensity was found
    * @return 
    */
   public int getTargetBearing()
   {
	   return _targetBearing;
   }
   
   /**
    * returns the maximum light intensity found during the scan
    * @return  light intensity
    */
   public int getLight()
   {
      return _maxLight;
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
    * sets the motor sped in deg/sec
    * @param speed 
    */
   public void setSpeed(int speed)
   {
      motor.setSpeed(speed);
   }

   /**
    * scan from current head angle to limit angle and determine/store the best angle,
    * the angle with the maximum light intensity.
    * @param limitAngle 
    */
   public void scanTo(int limitAngle)
   {
	  _maxLight = 0; //set max light intensity to 0
      int angle; //set variable oldAngle to get head angle
      motor.rotateTo(limitAngle, true); //rotate from head angle to limit angle
      int Light = _eye.getLightValue(); //set variable Light to retrieve light value
      while (motor.isMoving()) //while the motor is moving
      {
    	  int newLight = _eye.getLightValue(); //set variable newLight to get light value
    	  angle = motor.getTachoCount();
         /*
          * If angle is not equal to oldAngle, then check to see if newLight (the light value
          * at angle) is greater than light, the light value at oldAngle. If so, then set 
          * Light to equal newLight. Then check to see if newLight is greater than _maxLight.
          * If so, then set _maxLight to equal newLight, and set the max angle to be at angle.
          */
        	if (newLight > Light)
        	{
           		Light = newLight;
           		if (newLight>_maxLight)
           		{
           			_maxLight=newLight;
           			_targetBearing = angle;
           		}
        	}
      }
   }

   /******* instance variabled ***************/
   NXTRegulatedMotor motor;
   LightSensor _eye;
   int _targetBearing;
   int _maxLight;
   boolean _found = false;
}

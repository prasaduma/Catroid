/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.robot.albert;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

//This code is based on the nxt-implementation
public class RobotAlbert {

	private static RobotAlbertCommunicator myCommunicator;
	private static final int MOTOR_COMMAND = 102;
	private static final int BUZZER_COMMAND = 104;
	private static final int RGB_EYE_COMMAND = 105;
	private static final int FRONT_LED_COMMAND = 106;
	private static final String TAG = RobotAlbert.class.getSimpleName();

	private static Handler btcHandler;
	private Handler albertReceiverHandler;
	private Activity activity;

	public RobotAlbert(Activity parentActivity, Handler receiverHandler) {
		activity = parentActivity;
		albertReceiverHandler = receiverHandler;
	}

	public void startBTCommunicator(String macAddress) {

		if (myCommunicator != null) {
			try {
				myCommunicator.destroyConnection();
			} catch (IOException ioException) {
				Log.e(TAG, "Error destroying connection", ioException);
			}
		}

		myCommunicator = new RobotAlbertBtCommunicator(this, albertReceiverHandler, BluetoothAdapter.getDefaultAdapter(),
				activity.getResources());
		btcHandler = myCommunicator.getHandler();

		((RobotAlbertBtCommunicator) myCommunicator).setMACAddress(macAddress);
		myCommunicator.start();
	}

	public void destroyCommunicator() {

		if (myCommunicator != null) {
			try {
				myCommunicator.stopAllMovement();
				myCommunicator.destroyConnection();
			} catch (IOException ioException) {
				Log.e(TAG, "Error destroying connection", ioException);
			}
			myCommunicator = null;
		}
	}

	public void pauseCommunicator() {
		myCommunicator.stopAllMovement();
	}

	public static synchronized void sendRobotAlbertMotorMessage(int motor, int speed) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("motor", motor);
		myBundle.putInt("speed", speed);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = MOTOR_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static synchronized void sendRobotAlbertBuzzerMessage(int buzzer) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("buzzer", buzzer);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = BUZZER_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static synchronized void sendRobotAlbertFrontLedMessage(int status) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("frontLED", status);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = FRONT_LED_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static synchronized void sendRobotAlbertRgbLedEyeMessage(int eye, int red, int green, int blue) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("eye", eye);
		myBundle.putInt("red", red);
		myBundle.putInt("green", green);
		myBundle.putInt("blue", blue);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = RGB_EYE_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static int getRobotAlbertDistanceSensorLeftMessage() {
		return myCommunicator.sensors.getValueOfLeftDistanceSensor();
	}

	public static int getRobotAlbertDistanceSensorRightMessage() {
		return myCommunicator.sensors.getValueOfRightDistanceSensor();
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

}

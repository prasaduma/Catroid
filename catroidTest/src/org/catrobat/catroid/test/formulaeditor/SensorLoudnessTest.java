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
 */
package org.catrobat.catroid.test.formulaeditor;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.SimulatedAudioRecord;
import org.catrobat.catroid.utils.MicrophoneGrabber;

public class SensorLoudnessTest extends InstrumentationTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		SimulatedAudioRecord simRecorder = new SimulatedAudioRecord();
		Reflection.setPrivateField(MicrophoneGrabber.getInstance(), "audioRecord", simRecorder);

	}

	@Override
	public void tearDown() throws Exception {
		Reflection.setPrivateField(SensorLoudness.class, "instance", null);
		Reflection.setPrivateField(MicrophoneGrabber.getInstance(), "instance", null);
		super.tearDown();
	}

	public void testLoudnessChange() {
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		double startLoudness = SensorHandler.getSensorValue(Sensors.LOUDNESS);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		assertNotSame("Loudness value wasn't updated.", SensorHandler.getSensorValue(Sensors.LOUDNESS), startLoudness);

		SensorHandler.stopSensorListeners();
	}

	public void testMicRelease() {
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals("LoudnessSensor did not require shared microphone, isRecording()", true, MicrophoneGrabber
				.getInstance().isRecording());

		SensorHandler.stopSensorListeners();
		getInstrumentation().waitForIdleSync();
		assertEquals("LoudnessSensor did not release shared microphone, isRecording()", false, MicrophoneGrabber
				.getInstance().isRecording());
	}
}

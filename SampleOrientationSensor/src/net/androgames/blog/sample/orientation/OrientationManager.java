package net.androgames.blog.sample.orientation;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Android Orientation Sensor Manager Archetype
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class OrientationManager {
	
	private static Sensor sensor;
	private static SensorManager sensorManager;
	// you could use an OrientationListener array instead
	// if you plans to use more than one listener
	private static OrientationListener listener;
	
	/** indicates whether or not Orientation Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Orientation Sensor is running */
	private static boolean running = false;
	
	/**
	 * Returns true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}
	
	/**
	 * Unregisters listeners
	 */
	public static void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Returns true if at least one Orientation sensor is available
	 */
	public static boolean isSupported() {
		if (supported == null) {
			if (Orientation.getContext() != null) {
				sensorManager = (SensorManager) Orientation.getContext()
						.getSystemService(Context.SENSOR_SERVICE);
				List<Sensor> sensors = sensorManager.getSensorList(
						Sensor.TYPE_ORIENTATION);
				supported = new Boolean(sensors.size() > 0);
			} else {
				supported = Boolean.FALSE;
			}
		}
		return supported;
	}
	
	/**
	 * Registers a listener and start listening
	 */
	public static void startListening(OrientationListener orientationListener) {
		sensorManager = (SensorManager) Orientation.getContext()
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(
				Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
			running = sensorManager.registerListener(
					sensorEventListener, sensor, 
					SensorManager.SENSOR_DELAY_GAME);
			listener = orientationListener;
		}
	}

	/**
	 * The listener that listen to events from the orientation listener
	 */
	private static SensorEventListener sensorEventListener = new SensorEventListener() {
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
		public void onSensorChanged(SensorEvent event) {
			// forwards orientation to the OrientationListener
    		listener.onOrientationChanged(
    				event.values[0], 	// azimuth
    				event.values[1], 	// pitch
    				event.values[2]);	// roll
		}
		
	};

}

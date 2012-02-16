package net.androgames.level.orientation.provider;

import net.androgames.level.orientation.OrientationProvider;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.Surface;

/**
 * 
 * A Bubble level for Android phones
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 *
 */
public class ProviderAccelerometer extends OrientationProvider {
	
	private static OrientationProvider provider;
	
	private static final float[] GEOMAGNETIC_FIELD = new float[] {1, 1, 1};
	
	private ProviderAccelerometer() {
		super();
	}
	
	public static OrientationProvider getInstance() {
		if (provider == null) {
			provider = new ProviderAccelerometer();
		}
		return provider;
	}
 

	/**
	 * Calculate pitch and roll according to
	 * http://android-developers.blogspot.com/2010/09/one-screen-turn-deserves-another.html
	 * @param event
	 */
	protected void handleSensorChanged(SensorEvent event) {
	    float[] R = new float[16];
	    float[] I = new float[16];

	    SensorManager.getRotationMatrix(R, I, event.values, GEOMAGNETIC_FIELD);

	    float[] actual_orientation = new float[3];
	    float[] outR = new float[16];
	    
	    switch (displayOrientation) {
	    case Surface.ROTATION_270:
		    SensorManager.remapCoordinateSystem(R, 
		    		SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outR);
	    	break;
	    case Surface.ROTATION_180:
		    SensorManager.remapCoordinateSystem(R, 
		    		SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR);
	    	break;
	    case Surface.ROTATION_90:
		    SensorManager.remapCoordinateSystem(R, 
		    		SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
	    	break;
	    case Surface.ROTATION_0:
    	default:
		    SensorManager.remapCoordinateSystem(R, 
		    		SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
	    	break;
	    }
	    
	    SensorManager.getOrientation(outR, actual_orientation);
	    pitch = (float) (actual_orientation[1] * 180 / Math.PI);
        roll = - (float) (actual_orientation[2] * 180 / Math.PI);
	}

	@Override
	protected int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}
	
}
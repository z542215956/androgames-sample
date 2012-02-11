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
public class ProviderOrientation extends OrientationProvider {
	
	private static OrientationProvider provider;
	
	private ProviderOrientation() {
		super();
	}
	
	public static OrientationProvider getInstance() {
		if (provider == null) {
			provider = new ProviderOrientation();
		}
		return provider;
	}

	protected void handleSensorChanged(SensorEvent event) {
        pitch = event.values[1];
        roll = event.values[2];
        
        /*
        Pitch, rotation around x-axis (-180 to 180), with positive values when the z-axis moves toward the y-axis.
		Roll, rotation around y-axis (-90 to 90), with positive values when the x-axis moves toward the z-axis. 
        */
        
	    switch (displayOrientation) {
	    case Surface.ROTATION_270:
	    	pitch = roll;
	    	roll = - pitch;
	    	break;
	    case Surface.ROTATION_180:
	    	pitch = - pitch;
	    	roll = - roll;
	    	break;
	    case Surface.ROTATION_90:
	    	pitch = - roll;
	    	roll = pitch;
	    	break;
	    case Surface.ROTATION_0:
    	default:
	    	break;
	    }
	}

	@Override
	protected int getSensorType() {
		return Sensor.TYPE_ORIENTATION;
	}

}
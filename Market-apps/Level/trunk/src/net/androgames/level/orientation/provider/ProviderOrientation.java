package net.androgames.level.orientation.provider;

import net.androgames.level.orientation.OrientationProvider;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
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
	
	private float tmp;
	
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

	    switch (displayOrientation) {
	    case Surface.ROTATION_270:
	    	pitch = - pitch;
	    	roll = - roll;
	    case Surface.ROTATION_90:
	    	tmp = pitch;
	    	pitch = - roll;
	    	roll = tmp;
	    	if (roll > 90) {
	    		roll = 180 - roll;
	    		pitch = - pitch - 180;
	    	} else if (roll < -90) {
	    		roll = - roll - 180;
	    		pitch = 180 - pitch;
	    	}
	    	break;
	    case Surface.ROTATION_180:
	    	pitch = - pitch;
	    	roll = - roll;
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
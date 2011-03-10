package net.androgames.level.orientation.provider;

import net.androgames.level.orientation.OrientationProvider;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

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
 
    private float x;
    private float y;
    private float z2;
    private double norm;
	
	private ProviderAccelerometer() {}
	
	public static OrientationProvider getInstance() {
		if (provider == null) {
			provider = new ProviderAccelerometer();
		}
		return provider;
	}
 
    protected void handleSensorChanged(SensorEvent event) {
		// screen orientation fix
		if (screenConfig > 0) {
			if ((screenConfig & (1 << 0)) > 0) {
				// switch vertically
				event.values[0] = -event.values[0];
			}
			if ((screenConfig & (1 << 1)) > 0) {
				// switch horizontally
				event.values[1] = -event.values[1];
			}
			if ((screenConfig & (1 << 2)) > 0) {
				// invert X and Y axis
				tmp = event.values[0];
				event.values[0] = event.values[1];
				event.values[1] = tmp;
			}
		}
        x = event.values[0];
        y = event.values[1];
        z2 = event.values[2] * event.values[2];
        // calcul du pitch
        norm = Math.sqrt(x*x + z2);
        if (norm != 0) {
        	pitch = (float) (- Math.atan2(y, norm) * 180 / Math.PI);
        } else {
        	pitch = 0;
        }
        // calcul du roll
        norm = Math.sqrt(y*y + z2);
        if (norm != 0) {
        	roll = (float) (Math.atan2(x, norm) * 180 / Math.PI);
        } else {
        	roll = 0;
        }
    }

	@Override
	protected int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}
	
}
package net.androgames.blog.sample.orientation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Android orientation sensor tutorial
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class Orientation extends Activity implements OrientationListener {
	
	private static Context CONTEXT;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CONTEXT = this;
    }

    protected void onResume() {
    	super.onResume();
    	if (OrientationManager.isSupported()) {
    		OrientationManager.startListening(this);
    	}
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	if (OrientationManager.isListening()) {
    		OrientationManager.stopListening();
    	}
    	
    }

    public static Context getContext() {
		return CONTEXT;
	}

	@Override
	public void onOrientationChanged(float azimuth, float pitch, float roll) {
		((TextView) findViewById(R.id.azimuth)).setText(String.valueOf(azimuth));
		((TextView) findViewById(R.id.pitch)).setText(String.valueOf(pitch));
		((TextView) findViewById(R.id.roll)).setText(String.valueOf(roll));
	}
    
}
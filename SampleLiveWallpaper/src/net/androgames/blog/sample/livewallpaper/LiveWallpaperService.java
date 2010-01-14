package net.androgames.blog.sample.livewallpaper;

import android.content.SharedPreferences;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveWallpaperService extends WallpaperService {
	
	public static final String PREFERENCE_SPEED = "PREFERENCE_SPEED";

	@Override
	public Engine onCreateEngine() {
		return new SampleEngine();
	}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

	public class SampleEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

		private LiveWallpaperPainting painting;
		private SharedPreferences prefs;
		
		SampleEngine() {
			SurfaceHolder holder = getSurfaceHolder();
	        painting = new LiveWallpaperPainting(holder, getApplicationContext());
	        prefs = LiveWallpaperService.this.getSharedPreferences(PREFERENCE_SPEED, 0);
	        prefs.registerOnSharedPreferenceChangeListener(this);
		}

		public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {}

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // remove listeners and callbacks here
            painting.stopPainting();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                painting.resumePainting();
            } else {
                // remove listeners and callbacks here
            	painting.pausePainting();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            painting.setSurfaceSize(width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            painting.start();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            boolean retry = true;
            painting.stopPainting();
            while (retry) {
                try {
                	painting.join();
                    retry = false;
                } catch (InterruptedException e) {}
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, 
        		float xStep, float yStep, int xPixels, int yPixels) {
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            painting.doTouchEvent(event);
        }
		
	}
	
}

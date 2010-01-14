package net.androgames.blog.sample.livewallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveWallpaperPainting extends Thread {

    /** Reference to the View and the context */
    private SurfaceHolder surfaceHolder;
	private Context context;
	
	/** State */
	private boolean wait;
	private boolean run;
	
	/** Dimensions */
	private int width;
	private int height;
	
    public LiveWallpaperPainting(SurfaceHolder surfaceHolder, Context context) {
    	// keep a reference of the context and the surface
    	// the context is needed is you want to inflate
    	// some resources from your livewallpaper .apk
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        // don't animate until surface is created and displayed
        this.wait = true;
    }

    /**
     * Pauses the livewallpaper animation
     */
    public void pausePainting() {
        this.wait = true;
        synchronized(this) {
        	this.notify();
        }
    }

    /**
     * Resume the livewallpaper animation
     */
    public void resumePainting() {
    	this.wait = false;
        synchronized(this) {
        	this.notify();
        }
    }

    /**
     * Stop the livewallpaper animation
     */
    public void stopPainting() {
    	this.run = false;
        synchronized(this) {
        	this.notify();
        }
    }

    @Override
    public void run() {
    	this.run = true;
		Canvas c = null;
    	while (run) {
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    doDraw(c);
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            // pause if no need to animate
            synchronized (this) {
	            if (wait) {
	                try {
	                    wait();
	                } catch (Exception e) {}
	            }
            }
        }
    }

    /**
     * Invoke when the surface dimension change
     * 
     * @param width
     * @param height
     */
    public void setSurfaceSize(int width, int height) {
        this.width = width;
        this.height = height;
        synchronized(this) {
        	this.notify();
        }
    }
    
    public void doTouchEvent(MotionEvent event) {
        synchronized(this) {
        	notify();
        }
    }

    private void doDraw(Canvas canvas) {
    	canvas.drawColor((int) (Math.random() * 0xFFFFFFFF));
    }
    
}

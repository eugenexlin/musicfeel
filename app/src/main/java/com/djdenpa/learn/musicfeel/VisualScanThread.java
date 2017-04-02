package com.djdenpa.learn.musicfeel;

import android.graphics.Canvas;

import static android.os.SystemClock.elapsedRealtime;

/**
 * Created by H on 2016/02/15.
 */
public class VisualScanThread extends Thread {
    private VisualScanView view;
    private boolean isRunning = true;

    public VisualScanThread(VisualScanView view) {
        this.view = view;
    }

    public void Terminate() {
        isRunning = false;
    }


    @Override
    public void run() {
        while (isRunning) {
            //fetch data for drawing
            view.updateScreenInterfaceLogic();

            Canvas c = null;
            try {
                c = view.getHolder().lockCanvas();
                if (c == null){
                    Thread.sleep(100);
                    continue;
                }
                synchronized (view.getHolder()) {
                    view.render(c);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}
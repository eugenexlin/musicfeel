package com.djdenpa.learn.musicfeel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Scroller;

import com.djdenpa.learn.musicfeel.screenDrawer.FFTScreenDrawer;
import com.djdenpa.learn.musicfeel.screenDrawer.IScreenDrawer;
import com.djdenpa.learn.musicfeel.tools.DebugLogger;

import org.apache.log4j.Logger;

/**
 * Created by H on 2016/02/15.
 */
public class VisualScanView extends SurfaceView implements SurfaceHolder.Callback{

    Logger log = DebugLogger.getLogger(getClass());

    private SurfaceHolder holder;
    private VisualScanThread thread;
    private WindowManager wm;

    public String mFilePath;

    public float mMinX = 0;
    public float mMinY = 0;
    public float mMaxX = 10000;
    public float mMaxY = 10000;

    protected IScreenDrawer mDrawer;

    public VisualScanView(Context context) {
        super(context);

        int newVis = SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE;
        setSystemUiVisibility(newVis);

        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScrollerX = new Scroller(context);
        mScrollerY = new Scroller(context);
        mNormPaint.setColor(Color.WHITE);
        mNormPaint.setTextSize(50);
        mFaintPaint.setColor(Color.argb(100,255,255,255));

        setFocusable(true);
        getHolder().addCallback(this);

        mFilePath = context.getFilesDir() + "/out.wav";
        mDrawer = new FFTScreenDrawer(this);
        mDrawer.initialize();
    }

    public float mScaleFactor = 1.0f;
    private float mGridSpacing = 200;

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleChange = detector.getScaleFactor();
            applyScale(scaleChange);
            invalidate();
            return true;
        }
    }

    public void applyScale(float pScale){

        float prevScaleFactor = mScaleFactor;
        mScaleFactor *= pScale;
        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 4.0f));

        float actualScale = mScaleFactor/prevScaleFactor;

        float dx = getWidth()/prevScaleFactor/2 * (1-actualScale);
        float dy = getHeight()/prevScaleFactor/2 * (1-actualScale);

        mPosX -= dx;
        mPosY -= dy;

    }

    static final int INVALID_POINTER_ID = -1;

    private ScaleGestureDetector mScaleDetector;
    public Scroller mScrollerX;
    private GestureDetector mGestureDetectorX = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    // beware, it can scroll to infinity
                    mPosX += distanceX / mScaleFactor;
                    //mPosY += distanceY;
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                    mScrollerX.fling( (int)mPosX,0,
                            (int) (-vX / mScaleFactor), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                    invalidate(); // don't remember if it's needed
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    if(!mScrollerX.isFinished() ) { // is flinging
                        mScrollerX.forceFinished(true); // to stop flinging on touch
                    }
                    return true; // else won't work
                }
            });

    public Scroller mScrollerY;
    private GestureDetector mGestureDetectorY = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    // beware, it can scroll to infinity
                    //mPosX += distanceX;
                    mPosY += distanceY / mScaleFactor;
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                    mScrollerY.fling( 0,(int)mPosY,
                            0, (int) (-vY / mScaleFactor), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    invalidate(); // don't remember if it's needed
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    if(!mScrollerY.isFinished() ) { // is flinging
                        mScrollerY.forceFinished(true); // to stop flinging on touch
                    }
                    return true; // else won't work
                }
            });

    public int mActivePointerId = INVALID_POINTER_ID;
    public float mPosX = 0;
    public float mPosY = 0;
    public float mLastTouchX = 0;
    public float mLastTouchY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);
        mGestureDetectorX.onTouchEvent(ev);
        mGestureDetectorY.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = ev.getX();
                final float y = ev.getY();

                // Save the ID of this pointer (for dragging)
                mActivePointerId = ev.getPointerId(pointerIndex);

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }


            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;

                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId  = ev.getPointerId(pointerIndex);
                log.info(pointerId);
                if(pointerId >= 2){
                    try{
                        FFTScreenDrawer draw =   ((FFTScreenDrawer) mDrawer);
                        draw.channel = (draw.channel+1) % 2;
                    }catch(Exception ex)
                    {}
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId  = ev.getPointerId(pointerIndex);
                final float x = ev.getX();
                final float y = ev.getY();

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);

                }
                break;
            }
        }
        return true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new VisualScanThread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (thread != null) {
            thread.Terminate();
            while (retry) {
                try {
                    thread.join();
                    mDrawer.terminate();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }
        thread = null;
    }

    private Paint mNormPaint = new Paint();
    private Paint mFaintPaint = new Paint();

    public void render(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        if (mDrawer != null) {
            float maxX = mDrawer.getMaxX();
            if (maxX < getWidth()){
                maxX = getWidth();
            }
            mMaxX = maxX;
            float maxY = mDrawer.getMaxY();
            if (maxY < getHeight()){
                maxY = getHeight();
            }
            mMaxY = maxY;
            mDrawer.render(canvas);
        }else{
            canvas.drawColor(Color.BLACK);
        }

        canvas.drawText(""+mScaleFactor, 100, 100, mNormPaint);
        canvas.drawText("x = " + mPosX, 100, 300, mNormPaint);
        canvas.drawText("y = " + mPosY, 100, 400, mNormPaint);

    /*    if (mActivePointerId != INVALID_POINTER_ID) {
            canvas.drawCircle(mLastTouchX, mLastTouchY, 100, mNormPaint);
        }*/

        //draw a grid.
        boolean toggle;

        int startingIndexY = (int)(mPosY/mGridSpacing);
        float startingGridY = startingIndexY*mGridSpacing;
        float offsetY =  (mPosY - startingGridY) * mScaleFactor ;

        toggle= ((startingIndexY % 2) == 0);
        float iterateY = 0;
        float actualY = -offsetY;
        while (actualY < getHeight()){
            canvas.drawLine(0, actualY, getWidth(), actualY, mFaintPaint);
            iterateY += mGridSpacing;
            float nextActualY = iterateY * mScaleFactor - offsetY;
            if (toggle){
                canvas.drawRect(0, actualY, 5, nextActualY, mFaintPaint);
            }
            actualY= nextActualY;
            toggle = !toggle;
        }

        int startingIndexX = (int)(mPosX/mGridSpacing);
        float startingGridX = startingIndexX*mGridSpacing;
        float offsetX =  (mPosX - startingGridX) * mScaleFactor ;

        toggle= ((startingIndexX % 2) == 0);
        float iterateX = 0;
        float actualX = -offsetX;
        while (actualX < getWidth()){
            canvas.drawLine(actualX, 0, actualX, getHeight(), mFaintPaint);
            iterateX += mGridSpacing;
            float nextActualX = iterateX * mScaleFactor - offsetX;
            if (toggle){
                canvas.drawRect(actualX, 0 , nextActualX, 5, mFaintPaint);
            }
            actualX = nextActualX;
            toggle = !toggle;
        }

    }

    //bigger is slower
    protected float mEaseInFactor = 5.0f;

    public void updateScreenInterfaceLogic(){
        //when is not touching, round to the nearest .25 scale?
        if (mActivePointerId == INVALID_POINTER_ID){
            float nearestScale = Math.round(mScaleFactor*4.0f)/4.0f;
            if (Math.abs(mScaleFactor - nearestScale) < .05) {
                mScaleFactor = nearestScale;
            }else{
                float nextScale = (nearestScale + mScaleFactor * mEaseInFactor)/(1+mEaseInFactor);
                float changeScale = nextScale/mScaleFactor;
                applyScale(changeScale);
            }
        }

        if (mScrollerX.computeScrollOffset()){
            mPosX = mScrollerX.getCurrX();
        }
        if (mScrollerY.computeScrollOffset()){
            mPosY = mScrollerY.getCurrY();
        }

        boolean isOutBoundX = false;
        if (mPosX < mMinX) {
            isOutBoundX = true;
        }
        if (mPosX > mMaxX) {
            isOutBoundX = true;
        }
        boolean isOutBoundY = false;
        if (mPosY < mMinY) {
            isOutBoundY = true;
        }
        if (mPosY > mMaxY) {
            isOutBoundY = true;
        }

        if (isOutBoundX){
            float scX= mScrollerX.getCurrX();
            float sfX= mScrollerX.getFinalX();
            if (Math.abs(scX - sfX) > 10) {
                mScrollerX.setFinalX((int) ((scX + sfX) / 2));
            }else{
                mScrollerX.forceFinished(true);
            }
        }
        if (isOutBoundY) {
            float scY = mScrollerY.getCurrY();
            float sfY = mScrollerY.getFinalY();
            if (Math.abs(scY - sfY) > 10) {
                mScrollerY.setFinalY((int) ((scY + sfY) / 2));
            } else {
                mScrollerY.forceFinished(true);
            }
        }

        if (mPosX < mMinX) {
            mScrollerX.forceFinished(true);
            if (Math.abs(mPosX-mMinX) < 3) {
                mPosX = mMinX;
            }else{
                mPosX = (mMinX+mPosX*mEaseInFactor)/(mEaseInFactor+1);
            }
        }
        float targetMaxX = mMaxX - getWidth()/mScaleFactor;
        if (mPosX > targetMaxX) {
            mScrollerX.forceFinished(true);
            if (Math.abs(mPosX-targetMaxX) < 3) {
                mPosX = targetMaxX;
            }else{
                mPosX = (targetMaxX+mPosX*mEaseInFactor)/(mEaseInFactor+1);
            }
        }
        if (mPosY < mMinY) {
            mScrollerY.forceFinished(true);
            if (Math.abs(mPosY-mMinY) < 3) {
                mPosY = mMinY;
            }else{
                mPosY = (mMinY+mPosY*mEaseInFactor)/(mEaseInFactor+1);
            }
        }
        float targetMaxY = mMaxY - getHeight()/mScaleFactor;
        if (mPosY > targetMaxY) {
            mScrollerY.forceFinished(true);
            if (Math.abs(mPosY-targetMaxY) < 3) {
                mPosY = targetMaxY;
            }else{
                mPosY = (targetMaxY+mPosY*mEaseInFactor)/(mEaseInFactor+1);
            }
        }



    }


}

package com.djdenpa.learn.musicfeel.screendrawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.djdenpa.learn.musicfeel.VisualScanView;
import com.djdenpa.learn.musicfeel.tools.DebugLogger;

import org.apache.log4j.Logger;

/**
 * Created by H on 2017/02/04.
 */

public class FFTScreenDrawer implements IScreenDrawer {

    Logger log = DebugLogger.getLogger(getClass());

    float mMaxX;
    @Override
    public float getMaxX() {
        return mMaxX;
    }

    float mMaxY;
    @Override
    public float getMaxY() {
        return mMaxY;
    }

    static final int PIXEL_SIZE = 16;
    public int channel = 0;

    VisualScanView mView;

    public FFTScreenDrawer(VisualScanView pView){
        mView= pView;

        mFeelPaint.setColor(Color.GREEN);

        mErrorPaint.setColor(Color.RED);
        mErrorPaint.setTextSize(50);

        mFeel2Paint.setColor(Color.MAGENTA);

        mTestPaint.setTextSize(PIXEL_SIZE);
        mTestPaint.setColor(Color.WHITE);
    }

    private Paint mErrorPaint = new Paint();
    private Paint mFeelPaint = new Paint();
    private Paint mFeel2Paint = new Paint();
    private Paint mTestPaint = new Paint();

    @Override
    public void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        if (!initialized){
            return;
        }

        float scaledPixelSize = PIXEL_SIZE * mView.mScaleFactor;

        float textX = (100 - mView.mPosX) * mView.mScaleFactor;
        mTestPaint.setTextSize(scaledPixelSize);

        int startIndexX = (int) (mView.mPosX / PIXEL_SIZE);
        if (startIndexX < 0){
            startIndexX = 0;
        }
        float offsetX = (startIndexX * PIXEL_SIZE - mView.mPosX) * mView.mScaleFactor;

        int indexY = (int) (mView.mPosY / PIXEL_SIZE);
        if (indexY < 0){
            indexY = 0;
        }
        int rowIndex = 0;
        float offsetY = (indexY * PIXEL_SIZE - mView.mPosY) * mView.mScaleFactor;
        float currentY = offsetY;

        int sumWidth = 50;

        while(indexY < FFTData[channel].length && currentY < mView.getHeight()){

            float currentX = offsetX;
            int indexX = startIndexX;
            int colIndex = 0;
            while(indexX < FFTData[channel][indexY].length && currentX < mView.getWidth()){
                float val = (int) (FFTData[channel][indexY][indexX]);
                val = val*5.5f + 0;
                if (val > 255){
                    val = 255;
                }
                if (val < 0){
                    val = 0;
                }
                mFeelPaint.setAlpha((int) val);
                canvas.drawRect(currentX+sumWidth, currentY, currentX+scaledPixelSize-1+sumWidth, currentY + scaledPixelSize-1, mFeelPaint);

                indexX += 1;
                colIndex += 1;
                currentX = offsetX + colIndex * scaledPixelSize;
            }

            //canvas.drawText("sum " + sum, textX, currentY + mTestPaint.getTextSize(), mTestPaint);
            float sum = 0.0f;
            for(int i = 0; i < FFTData[channel][indexY].length; i++){
                sum += FFTData[channel][indexY][i];
            }
            sum = sum * 600 / FFTData[channel][indexY].length;
            canvas.drawRect(offsetX+sum, currentY, offsetX+sum+5, currentY + 5, mFeel2Paint);

            indexY += 1;
            rowIndex += 1;
            currentY = offsetY + rowIndex * scaledPixelSize;
        }


        if (mFFTRunnable.hasError){
            canvas.drawText("ERROR",0,0,mErrorPaint);
        }
    }

    Thread FFTThread;
    FFTScreenDrawerRunnable mFFTRunnable;
    //[channel][step][values]
    public float[][][] FFTData;

    protected boolean initialized = false;

    @Override
    public void initialize() {
        mFFTRunnable = new FFTScreenDrawerRunnable(mView.mFilePath);
        FFTData = mFFTRunnable.initData();
        mMaxX = mFFTRunnable.mFFTSize*PIXEL_SIZE;
        mMaxY = FFTData[channel].length*PIXEL_SIZE;
        FFTThread = new Thread(mFFTRunnable);
        FFTThread.start();
        initialized = true;
    }

    @Override
    public void terminate() {
        mFFTRunnable.terminate();
    }
}

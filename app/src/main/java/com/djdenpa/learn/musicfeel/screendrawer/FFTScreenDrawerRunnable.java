package com.djdenpa.learn.musicfeel.screendrawer;

import com.djdenpa.learn.musicfeel.tools.beatdetection.BeatBucket;
import com.djdenpa.learn.musicfeel.tools.beatdetection.BeatComparator;
import com.djdenpa.learn.musicfeel.tools.beatdetection.BeatDetector;
import com.djdenpa.learn.musicfeel.tools.DebugLogger;
import com.djdenpa.learn.musicfeel.tools.FFTFloat;
import com.djdenpa.learn.musicfeel.tools.FFTUtils;
import com.djdenpa.learn.musicfeel.tools.WavFile;
import com.djdenpa.learn.musicfeel.tools.WavFileException;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by H on 2017/02/04.
 */

public class FFTScreenDrawerRunnable implements Runnable {

    Logger log = DebugLogger.getLogger(getClass());

    public int mStepSize =  220;
    public int mFFTLogSize = 9;
    public int mFFTSize = (int) Math.pow(2,mFFTLogSize);
    private WavFile oWavFile;
    public boolean hasError = false;
    private FFTFloat mFFT;
    private boolean isRunning = true;

    public ArrayList<BeatDetector> beatDetectors = new ArrayList<>();

    public FFTScreenDrawerRunnable(String sFileName){
        try {
            oWavFile = WavFile.openWavFile(new File(sFileName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }

    }

    public float[][][] initData(){
        int channels = oWavFile.getNumChannels();
        int length = (int)oWavFile.getNumFrames();
        if (mStepSize > mFFTSize){
            mStepSize = mFFTSize;
        }
        int calcCount = length/mStepSize+3;
        //log.info("length: " + length);
        //log.info("array index: " + calcCount);
        int maxRows = 3000;
        if (calcCount > maxRows){
            calcCount = maxRows;
        }

        mFFTData = new float[channels][calcCount][mFFTSize];

        beatDetectors.clear();
        for (int i = 0; i < channels; i++){
            BeatDetector bd = new BeatDetector();
            bd.stepSize = mStepSize;
            beatDetectors.add(bd);
        }

        mFFT = new FFTFloat(mFFTSize);
        return mFFTData;
    }

    //[channel][step][values]
    public float[][][] mFFTData;

    public float[] realSet = new float[mFFTSize];
    public float[] imaginarySet = new float[mFFTSize];
    public float[] magnitude = new float[mFFTSize];

    @Override
    public void run() {
        int numChannels = oWavFile.getNumChannels();
        int framesRead;
        int bitRate = (int) oWavFile.getSampleRate();
        //log.info(bitRate);
        double[][] readBuffer = new double[numChannels][mStepSize];
        float[][] choochooBuffer = new float[numChannels][Math.max(mStepSize, mFFTSize)];
        int copyIndex = 0;

        do
        {
            if (!isRunning){
                break;
            }
            if (copyIndex >= mFFTData[0].length){
                break;
            }

            int copyOffset = mFFTSize - mStepSize;
            //inch down the buffer for area not covered by step of reading WAV
            if(copyOffset > 0) {
                for (int channel = 0; channel < readBuffer.length; channel++) {
                    System.arraycopy(choochooBuffer[channel], 0 + mStepSize, choochooBuffer[channel], 0, copyOffset);
                }
            }

            try {
                framesRead = oWavFile.readFrames(readBuffer, mStepSize);
            } catch (Exception e) {
                e.printStackTrace();
                hasError = true;
                return;
            }

            for (int channel = 0; channel < readBuffer.length; channel++) {
                for(int index = 0; index < readBuffer[channel].length; index++){
                    choochooBuffer[channel][index+copyOffset] = (float) readBuffer[channel][index];
                }
            }

            for (int channel = 0; channel < readBuffer.length; channel++) {
                System.arraycopy(choochooBuffer[channel], 0, realSet, 0, mFFTSize);
                Arrays.fill(imaginarySet, 0);
                mFFT.fft(realSet, imaginarySet);
                //calcFFTReal(realSet, imaginarySet, mFFTLogSize);
                //GeneralFFT.transform(realSet,imaginarySet);

                float sum = 0.0f;
                for(int i = 0; i<mFFTSize; i++){
                    float mag = (float) Math.sqrt(realSet[i]*realSet[i] + imaginarySet[i]*imaginarySet[i]);
                    mag = FFTUtils.calculatePercievedVolume(mag,i,mFFTSize,bitRate);
                    magnitude[i] = mag;
                    sum += mag;
                }

                beatDetectors.get(channel).feedNextStep(sum);

                System.arraycopy(magnitude, 0, mFFTData[channel][copyIndex], 0, mFFTSize);
            }
            copyIndex += 1;
        }
        while (framesRead != 0);

        log.info("FTT FINISH");
        BeatComparator comp = new BeatComparator();
        for (BeatDetector bt : beatDetectors){
            log.info("ONE CHAAN");
            Collections.sort(bt.buckets, comp);
            for (BeatBucket bb : bt.buckets){
                log.info("dist - " + bb.averageDistance + " ; strength - " + bb.currentFeel);
            }
        }

    }

    public void terminate(){
        isRunning = false;
    }



    //private native float[] calcFFTReal(float[] real, float[] imaginary, int size);

    static {
        System.loadLibrary("MusicFeelNLib");
    }
}

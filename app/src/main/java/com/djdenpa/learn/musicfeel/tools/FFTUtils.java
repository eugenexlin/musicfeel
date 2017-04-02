package com.djdenpa.learn.musicfeel.tools;

import android.util.Log;

import junit.framework.Assert;

/**
 * Created by H on 2017/02/11.
 */

public class FFTUtils {
    //linearly scale how the ear will percieve the volume 0 ~ 10,000 by 1000
    static float[] volumeScale = {
            0.35f,
            1.00f,
            1.06f,
            1.16f,
            1.22f,
            1.15f,
            1.05f,
            0.94f,
            0.91f,
            0.88f,
            0.85f};


    //then for 15,000 use 1.1
    //then for 22,000 use 0.0

    public static float calculatePercievedVolume(float inputPCM, float inBucketIndex, float inBucketCount, int inBitRate){
        float currentHZ = inBucketIndex*inBitRate/inBucketCount;
        //System.out.println(""+currentHZ);  for unit test.
        float factor = 1.0f;
        if (currentHZ < 10000){
            //offset of bucket.
            int indexThousand = (int) (currentHZ / 1000);

            int lower = indexThousand*1000;
            int upper = (indexThousand+1)*1000;

            Assert.assertTrue(lower<=currentHZ);
            Assert.assertTrue(currentHZ<=upper);

            float lowerScale = (upper - currentHZ)*volumeScale[indexThousand]/1000;
            float upperScale = (currentHZ - lower)*volumeScale[indexThousand+1]/1000;
            factor = lowerScale + upperScale;

        }else if(currentHZ < 15000){
            float lowerScale = (15000 - currentHZ)*(0.85f)/5000;
            float upperScale = (currentHZ - 10000)*(1.1f)/5000;
            factor = lowerScale + upperScale;

        }else if(currentHZ < 22000){
            float lowerScale = (22000 - currentHZ)*(1.1f)/7000;
            factor = lowerScale;
        }else{
            return 0;
        }

        return inputPCM*factor;
    }
}

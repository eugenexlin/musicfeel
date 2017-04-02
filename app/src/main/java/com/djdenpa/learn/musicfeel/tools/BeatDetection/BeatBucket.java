package com.djdenpa.learn.musicfeel.tools.BeatDetection;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by H on 2017/02/12.
 */

public class BeatBucket {
    //the number a new beat will compare against to see if they should be part of this bucket.
    public float averageDistance = 0.0f;

    //feel factor that grows strongly for positive reinforcement, and decays slowly.
    public float currentFeel = 0.0f;

    //private ArrayList<BeatItem> beats = new ArrayList<>();
    private float itemCount = 0.0f;
    private float itemCountMax = 20.0f;

    public BeatBucket(){
    //    beats =  new ArrayList<>();
    }

    public boolean testFit(float distance, int stepSize){
        return  (Math.abs(distance - averageDistance) < stepSize*(0.5f + (itemCountMax-itemCount)/(2*itemCountMax)));
    }

    public void addItem(float distance, float strength){
        currentFeel += strength;
        if (itemCount > 1) {
            float changeDiff = distance - averageDistance;
            changeDiff /= itemCount;
            averageDistance += changeDiff;
        }else{
            averageDistance = distance;
        }
        if(itemCount < itemCountMax) {
            itemCount += 1.0f;
        }
    }

}

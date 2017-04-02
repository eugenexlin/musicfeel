package com.djdenpa.learn.musicfeel.tools.BeatDetection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

/**
 * Created by H on 2017/02/12.
 *
 *
 */

public class BeatDetector {

    public BeatBucket bucket = new BeatBucket();

    public int bitRate = 44010;
    public int stepSize = 200;

    //track the largest spike. for luck
    private float biggestGain;

    public long currentSeek = 0;

    //i guess kind of get the current value
    private float currentStrength = 0;

    //i guess kind of get the current value
    private float previousStartBeat = 0;
    private long previousStartSeek = 0;

    //track beat gone upwards'
    private boolean isInBeat = false;

    public BeatDetector(){

        buckets = new ArrayList<>();
        recentBeats = new LinkedList<>();
    }



    public void feedNextStep(float pStrength){
        if (!isInBeat) {
            if(pStrength > currentStrength) {
                isInBeat = true;
                previousStartBeat = pStrength;
                previousStartSeek = currentSeek;
            }
        }else{
            if(pStrength < currentStrength) {
                //we fell so use last beat
                float gains = (currentStrength - previousStartBeat);
                isInBeat = false;
                if (biggestGain < gains){
                    biggestGain = gains;
                }
                //only if beat is significant, register
                if (gains > biggestGain/5){
                    int probableStartingSeek = (int) ((previousStartSeek + currentSeek) /2);
                    BeatItem item = new BeatItem(probableStartingSeek, gains);
                    processNewBeatItem(item);
                }

            }
        }
        currentStrength = pStrength;
        currentSeek += stepSize;
        biggestGain *= .99;
    }

    public ArrayList<BeatBucket> buckets;

    private LinkedList<BeatItem> recentBeats;
    public int recentBeatCount = 3;


    public void processNewBeatItem(BeatItem pItem){

        float multiplier = 1.0f;

        // Generate an iterator. Start just after the last element.
        ListIterator li = recentBeats.listIterator(recentBeats.size());

        // Iterate in reverse.
        while(li.hasPrevious()) {
            BeatItem recentItem = (BeatItem) li.previous();
            boolean isAdded = false;
            float distance = (float) (pItem.seekPoint-recentItem.seekPoint);


             for (BeatBucket bucket: buckets){
                 if(bucket.testFit(distance, stepSize)){
                     bucket.addItem(distance, pItem.strength*multiplier);
                     isAdded = true;
                 }
             }
            multiplier*= 0.8f;
            if (!isAdded){
                BeatBucket newOne = new BeatBucket();
                newOne.addItem(distance, pItem.strength);
                buckets.add(newOne);
            }
        }

        recentBeats.add(pItem);
        while (recentBeats.size() > recentBeatCount){
            recentBeats.poll();
        }

    }
}

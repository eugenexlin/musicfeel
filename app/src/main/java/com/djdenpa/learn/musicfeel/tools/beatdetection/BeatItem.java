package com.djdenpa.learn.musicfeel.tools.beatdetection;

/**
 * Created by H on 2017/02/12.
 *
 * data for Beat Bucket
 */

public class BeatItem {
    public long seekPoint;
    public float strength;

    public BeatItem(long pSeekPoint, float pStrength){
        seekPoint = pSeekPoint;
        strength = pStrength;
    }
}

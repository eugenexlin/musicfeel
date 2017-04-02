package com.djdenpa.learn.musicfeel.tools.BeatDetection;

/**
 * Created by H on 2017/02/12.
 */

public class BeatItem {
    public long seekPoint;
    public float strength;

    public BeatItem(long pSeekPoint, float pStrength){
        seekPoint = pSeekPoint;
        strength = pStrength;
    }
}

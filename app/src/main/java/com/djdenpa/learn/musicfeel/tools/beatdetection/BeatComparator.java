package com.djdenpa.learn.musicfeel.tools.beatdetection;

import java.util.Comparator;

/**
 * Created by H on 2017/02/12.
 *
 * Compares the importance of a beat bucket
 */

public class BeatComparator implements Comparator<BeatBucket> {

    @Override
    public int compare(BeatBucket o1, BeatBucket o2) {
        return Float.compare( o1.currentFeel,o2.currentFeel);
    }
}

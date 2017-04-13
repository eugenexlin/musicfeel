package com.djdenpa.learn.musicfeel.screendrawer;

import android.graphics.Canvas;

/**
 * Created by H on 2017/02/04.
 * Perhaps a nice base we can use to make a drawer for each activity
 */

public interface IScreenDrawer {
    float getMaxX();
    float getMaxY();

    void render(Canvas canvas);
    void initialize();
    void terminate();
}
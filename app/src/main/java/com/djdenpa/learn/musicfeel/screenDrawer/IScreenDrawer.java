package com.djdenpa.learn.musicfeel.screenDrawer;

import android.graphics.Canvas;

/**
 * Created by H on 2017/02/04.
 */

public interface IScreenDrawer {
    float getMaxX();
    float getMaxY();

    void render(Canvas canvas);
    void initialize();
    void terminate();
}
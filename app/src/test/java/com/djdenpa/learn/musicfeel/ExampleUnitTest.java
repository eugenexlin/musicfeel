package com.djdenpa.learn.musicfeel;

import android.util.Log;

import com.djdenpa.learn.musicfeel.tools.FFTUtils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void check () throws Exception {
        //should be 129 hz?
        float value;
        value = FFTUtils.calculatePercievedVolume(1.0f, 3,1024 ,44010);
        System.out.println("result "+ value);
        Assert.assertTrue(value < 0.5f);
        Assert.assertTrue(value > 0.3f);

        //988 hz
        value = FFTUtils.calculatePercievedVolume(1.0f, 23,1024 ,44010);
        System.out.println("result "+ value);
        Assert.assertTrue(value < 1.0f);
        Assert.assertTrue(value > 0.95f);

        //9500hz
        value = FFTUtils.calculatePercievedVolume(1.0f, 223,1024 ,44010);
        System.out.println("result "+ value);
        Assert.assertTrue(value < 0.88f);
        Assert.assertTrue(value > 0.85f);


        //14000 hz
        value = FFTUtils.calculatePercievedVolume(1.0f, 336,1024 ,44010);
        System.out.println("result "+ value);
        Assert.assertTrue(value < 1.10f);
        Assert.assertTrue(value > 1.05f);

        //12500 hz
        value = FFTUtils.calculatePercievedVolume(1.0f, 291,1024 ,44010);
        System.out.println("result "+ value);
        Assert.assertTrue(value < 0.98f);
        Assert.assertTrue(value > 0.96f);
    }
}
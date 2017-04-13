package com.djdenpa.learn.musicfeel.tools.dataanalysis;

import org.junit.Assert;
import org.junit.Test;

import com.djdenpa.learn.musicfeel.tools.dataanalysis.MusicData;

import java.util.UUID;

/**
 * Created by denpa on 4/12/2017.
 *
 * Test the main analyzer
 */

public class MusicDataTests {
  @Test

  //UUID should be pass by value or immutable or what not.. but i never tested it in Java.
  public void CheckUUIDPrivate() throws Exception {
    MusicData oData = new MusicData();
    UUID test = oData.id();

    String previous = test.toString();

    test = UUID.randomUUID();

    String testNext = test.toString();
    String idNext = oData.id().toString();

    Assert.assertNotEquals(previous,testNext);
    Assert.assertEquals(previous,idNext);
  }


}

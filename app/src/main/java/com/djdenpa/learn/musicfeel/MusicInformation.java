package com.djdenpa.learn.musicfeel;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by H on 2016/02/06.
 */
public class MusicInformation {

    public static String[] mediaStoreProjection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

    String artist, title, displayName, data;
    long id, duration;
    public MusicInformation(Cursor pCursor){
        id =
                Long.parseLong(pCursor.getString(getProjectionIndex(MediaStore.Audio.Media._ID)));
        duration =
                Long.parseLong(pCursor.getString(getProjectionIndex(MediaStore.Audio.Media.DURATION)));
        artist =
                pCursor.getString(getProjectionIndex(MediaStore.Audio.Media.ARTIST));
        title =
                pCursor.getString(getProjectionIndex(MediaStore.Audio.Media.TITLE));
        displayName =
                pCursor.getString(getProjectionIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        data =
                pCursor.getString(getProjectionIndex(MediaStore.Audio.Media.DATA));
    }

    public int getProjectionIndex(String str) {
        int k=-1;
        for(int i=0;i<mediaStoreProjection.length;i++){
            if(mediaStoreProjection[i]==str){
                k=i;
                break;
            }
        }
        return k;
    }

}

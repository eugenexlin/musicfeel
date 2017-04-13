package com.djdenpa.learn.musicfeel;

import com.djdenpa.learn.musicfeel.tools.DebugLogger;
import com.djdenpa.learn.musicfeel.tools.PCMAudioAnalyzer;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import org.apache.log4j.Logger;

public class VisualScanMusicActivity extends AppCompatActivity {

   Logger log = DebugLogger.getLogger(getClass());

    PCMAudioAnalyzer analyzer = null;
    public MusicInformation oMusicInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_visual_scan_music);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        String musicID = intent.getStringExtra(getString(R.string.selected_music_id));

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " = " + musicID;
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MusicInformation.mediaStoreProjection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        String sInPath = "";
        while(cursor.moveToNext()) {
            oMusicInfo = new MusicInformation(cursor);
            sInPath = oMusicInfo.data   ;
        }
        String sOutPath = getApplicationContext().getFilesDir() + "/out.wav";
        log.info(sOutPath);
        CreateWavFile(sInPath, sOutPath);


        analyzer = new PCMAudioAnalyzer();
        analyzer.analyzeMusicFile(sInPath);

        SurfaceView scanView = new VisualScanView(this);
        setContentView(scanView);

/*

        LinearLayout llBlocks = (LinearLayout)findViewById(R.id.llBlocks);

        Random oRand = new Random();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point oPoint = new Point();
        display.getSize(oPoint);
        int screenWidth = oPoint.x;
        int screenHeight = oPoint.y;
        int rowHeight = (screenHeight/ROWS_PER_SCREEN);

        int blocks = (int)(oMusicInfo.duration/10);

        if (blocks > 100000){
            throw new UnsupportedOperationException("making "+ blocks + " blocks you crazy.");
        }

        for (int i = 0; i < blocks; i++){

            ImageView oImage = new ImageView(this);
            llBlocks.addView(oImage);

            oImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(screenWidth,rowHeight));

            Bitmap oBitmap = Bitmap.createBitmap(screenWidth,rowHeight, Bitmap.Config.ARGB_8888);
            Canvas oCanvas = new Canvas(oBitmap);
            oCanvas.drawARGB(255, oRand.nextInt(255), 0, 0);

            oImage.setImageBitmap(oBitmap);

            // Adds the view to the layout

        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (analyzer != null){
            analyzer.Terminate();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }



    private native void CreateWavFile(String sInPath, String sOutPath);

    static {
        System.loadLibrary("MusicFeelNLib");
    }
}

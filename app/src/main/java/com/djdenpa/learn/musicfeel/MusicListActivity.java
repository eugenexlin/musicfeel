package com.djdenpa.learn.musicfeel;

import java.util.ArrayList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.provider.MediaStore;
import android.database.Cursor;
import android.content.pm.PackageManager;

public class MusicListActivity extends AppCompatActivity {

    private ArrayList<MusicInformation> songs = new ArrayList<>();
    private Context context;

    static final int  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else{
            bindListView();
        }

    }

    public void bindListView(){
        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MusicInformation.mediaStoreProjection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE+" ASC");

        while(cursor.moveToNext()) {

            try {
                MusicInformation item = new MusicInformation(cursor);
                if (item.data.contains(".wav")) {
                    songs.add(item);
                }
                if (item.data.contains(".mp3")) {
                    songs.add(item);
                }
            }catch(Exception ex){
                //der
            }
        }

        ArrayAdapter adapter = new MusicInformationAdapter(this,
                android.R.layout.test_list_item,
                songs);

        ListView lvList = (ListView)findViewById(R.id.lvSongs);
        lvList.setAdapter(adapter);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        bindListView();
                    }else if ( grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        TextView tvWarn = (TextView)findViewById(R.id.tvWarn);
                        tvWarn.setVisibility(View.VISIBLE);
                        //GG we are dead
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ClickMusic(View view) {
        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();

                Intent intent = new Intent(context, VisualScanMusicActivity.class);
                intent.putExtra(getString(R.string.selected_music_id), b.getString("MUSIC_ID"));
                startActivity(intent);

            }
        };
        TextView musicId = (TextView) view.findViewById(R.id.musicId);
        Message m = new Message();
        Bundle b = new Bundle();
        b.putString("MUSIC_ID", musicId.getText().toString());
        m.setData(b);
        h.sendMessageDelayed(m,200);
    }

}

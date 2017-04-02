package com.djdenpa.learn.musicfeel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by H on 2016/02/06.
 */
public class MusicInformationAdapter extends ArrayAdapter<MusicInformation> {

    private static class ViewHolder {
        private TextView title;
        private TextView artist;
        private TextView duration;
        private TextView musicId;
        public ViewHolder(View view){
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.artist);
            duration = (TextView) view.findViewById(R.id.duration);
            musicId = (TextView) view.findViewById(R.id.musicId);
        }
    }

    private ViewHolder viewHolder;

    public MusicInformationAdapter(Context context, int textViewResourceId, ArrayList<MusicInformation> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.music_layout, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MusicInformation item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.title.setText(item.title);
            viewHolder.artist.setText(item.artist);
            viewHolder.musicId.setText(String.valueOf(item.id));

            int seconds = (int)(Math.ceil(item.duration / 1000.0));
            int minutes = seconds/60;
            seconds = seconds%60;
            viewHolder.duration.setText(String.format("%d:%02d", minutes, seconds));
        }

        return convertView;
    }

}

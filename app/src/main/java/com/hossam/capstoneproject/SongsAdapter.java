package com.hossam.capstoneproject;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hossam.capstoneproject.activities.DetailsActivity;
import com.hossam.capstoneproject.models.SongModel;

import java.util.ArrayList;

/**
 * Created by Eli on 1/7/2018.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private static final String TAG = SongsAdapter.class.getSimpleName();
    ArrayList<SongModel> songModels;
    Activity activity;

    public SongsAdapter(ArrayList<SongModel> songModels, Activity activity) {
        this.songModels = songModels;
        this.activity = activity;
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_view_item, parent, false);
        Log.v(TAG, parent + "   <------- onCreateViewHolder");
        return new SongsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, final int position) {
        Log.v(TAG, position + "   <------- onBindViewHolder");

        holder.song_name.setText(songModels.get(position).getSongName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, DetailsActivity.class).putExtra("songModel", songModels.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return songModels.size();
    }

    class SongsViewHolder extends RecyclerView.ViewHolder {

        TextView song_name;

        public SongsViewHolder(View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
        }
    }
}

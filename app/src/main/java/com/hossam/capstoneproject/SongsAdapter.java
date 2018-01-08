package com.hossam.capstoneproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eli on 1/7/2018.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {


    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SongsViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.)

        public SongsViewHolder(View itemView) {
            super(itemView);
        }
    }
}

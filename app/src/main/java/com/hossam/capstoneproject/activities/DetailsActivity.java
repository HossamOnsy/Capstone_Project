package com.hossam.capstoneproject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hossam.capstoneproject.R;
import com.hossam.capstoneproject.models.SongModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.song_name)
    TextView song_name;
    @BindView(R.id.song_duration)
    TextView song_duration;
    Unbinder unbinder;
    SongModel songModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        unbinder= ButterKnife.bind(this);

        if(getIntent().hasExtra("songModel"))
            songModel = getIntent().getParcelableExtra("songModel");
        if (songModel != null)
        {
            try {
                String songName=getString(R.string.song_name) +songModel.getSongName();
                song_name.setText(songName);
                long seconds = Long.parseLong(songModel.getSongDuration()) / 1000;
                long minutes = seconds / 60;
                String seconds1=seconds% 60+"",minutes1=minutes% 60+"";
                if(seconds% 60<10)
                    seconds1 = "0"+seconds% 60;
                if(minutes% 60<10)
                    minutes1 = "0"+minutes% 60;

                String time =  minutes1  + ":" + seconds1;
                time = getString(R.string.duration) +time;
                song_duration.setText( time);
            }catch (Exception e){
                e.printStackTrace();
                song_duration.setText(getString(R.string.unknown));
            }
        }

    }
}

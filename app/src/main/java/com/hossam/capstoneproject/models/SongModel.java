package com.hossam.capstoneproject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eli on 1/7/2018.
 */

public class SongModel implements Parcelable {

    public static final Parcelable.Creator<SongModel> CREATOR = new Parcelable.Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel source) {
            return new SongModel(source);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };


    String songId = "";
    String songName = "";
    String songContentType = "";
    String songPath = "";
    String songCreationTimeMilis = "";


    public SongModel() {
    }

    protected SongModel(Parcel in) {
        this.songId = in.readString();
        this.songName = in.readString();
        this.songContentType = in.readString();
        this.songPath = in.readString();
        this.songCreationTimeMilis = in.readString();
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongContentType() {
        return songContentType;
    }

    public void setSongContentType(String songContentType) {
        this.songContentType = songContentType;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongCreationTimeMilis() {
        return songCreationTimeMilis;
    }

    public void setSongCreationTimeMilis(String songCreationTimeMilis) {
        this.songCreationTimeMilis = songCreationTimeMilis;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songId);
        dest.writeString(this.songName);
        dest.writeString(this.songContentType);
        dest.writeString(this.songPath);
        dest.writeString(this.songCreationTimeMilis);
    }
}

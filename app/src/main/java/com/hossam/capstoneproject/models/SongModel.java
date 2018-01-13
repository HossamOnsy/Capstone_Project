package com.hossam.capstoneproject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eli on 1/7/2018.
 */

public class SongModel implements Parcelable {



    String songId = "";
    String songName = "";
    String songPath = "";
    String songArtist = "";
    String songGenre = "";
    String songAuthor = "";
    String songDuration = "";

    public SongModel() {
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

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongGenre() {
        return songGenre;
    }

    public void setSongGenre(String songGenre) {
        this.songGenre = songGenre;
    }

    public String getSongAuthor() {
        return songAuthor;
    }

    public void setSongAuthor(String songAuthor) {
        this.songAuthor = songAuthor;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songId);
        dest.writeString(this.songName);
        dest.writeString(this.songPath);
        dest.writeString(this.songArtist);
        dest.writeString(this.songGenre);
        dest.writeString(this.songAuthor);
        dest.writeString(this.songDuration);
    }

    protected SongModel(Parcel in) {
        this.songId = in.readString();
        this.songName = in.readString();
        this.songPath = in.readString();
        this.songArtist = in.readString();
        this.songGenre = in.readString();
        this.songAuthor = in.readString();
        this.songDuration = in.readString();
    }

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
}

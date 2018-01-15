package com.hossam.capstoneproject.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hossam.capstoneproject.R;
import com.hossam.capstoneproject.SongsAdapter;
import com.hossam.capstoneproject.models.SongModel;
import com.hossam.capstoneproject.utils.SimpleAppWidgetProvider;
import com.hossam.capstoneproject.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * All The Commented Methods are for the Uploading to be completed as it was the main core for the app to be initiated
 *
 * @param
 */


public class MainActivity extends AppCompatActivity {
    //    private static final int FILE_SELECT_CODE = 0;
//    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    String s;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    //    @BindView(R.id.upload)
//    Button mUploadButton;
    @BindView(R.id.shuffle)
    Button shuffle;
    @BindView(R.id.get_a_different_list)
    Button get_a_different_list;
    @BindView(R.id.video_view)
    SimpleExoPlayerView video_view;
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String start = "0", end = "2";
    String maxid;
    ArrayList<SongModel> songModels;
    SimpleExoPlayer player;
    FirebaseDatabase database;
    Bundle bundle = null;
    boolean isPlayWhenReady = true;
    int lastKnownIndex = -1;

//    String METADATA_KEY_ARTIST = null;
//    String METADATA_KEY_AUTHOR = null;
//    String METADATA_KEY_DURATION = null;
//    String METADATA_KEY_GENRE = null;
//    String METADATA_KEY_TITLE = null;


    Unbinder unbinder;
    //    int i = 0;
    MediaSource mediaSource;
//    MediaMetadataRetriever mmr;
//    String albumName;
    boolean connected = false;

    @OnClick({R.id.shuffle, R.id.get_a_different_list})
    void View(View view) {
        switch (view.getId()) {
//            case R.id.upload: {
//                mUploadButton.setClickable(false);
//                progressBar.setVisibility(View.VISIBLE);
//                checkPermissions();
//            }
//            break;
            case R.id.get_a_different_list: {
                progressBar.setVisibility(View.VISIBLE);
                shuffleModeInFirebase(database);
            }
            break;
            case R.id.shuffle: {
                if (songModels != null) {
                    int jj = (int) (Math.random() * songModels.size());
                    lastKnownIndex = jj;
                    mediaSource = buildMediaSource(Uri.parse(songModels.get(jj).getSongPath()));
                    if (bundle != null)
                        isPlayWhenReady = bundle.getBoolean("playstate");
                    player.setPlayWhenReady(isPlayWhenReady);
                    player.prepare(mediaSource, true, false);
                }
            }
            break;


        }
    }

    void shuffleModeInFirebase(FirebaseDatabase database) {
        try {
            int x = 1;
            int y = 0;
            while (x > y) {
                x = (int) (Math.random() * Integer.parseInt(maxid));
                y = (int) (Math.random() * Integer.parseInt(maxid));

            }
            start = String.valueOf(x);
            end = String.valueOf(y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkConnectivity();
        if (connected) {
            DatabaseReference ref = database.getReference("songs");
            ref.orderByChild("songId").startAt(start).endAt(end).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    songModels = new ArrayList<>();
                    Log.v(TAG, dataSnapshot.toString());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.v(TAG, dataSnapshot1.toString());
                        SongModel songModel = dataSnapshot1.getValue(SongModel.class);
                        songModels.add(songModel);
                    }

                    SongsAdapter songsAdapter = new SongsAdapter(songModels, MainActivity.this);
                    recycler_view.setAdapter(songsAdapter);
                    try {
                        Log.v(TAG, songModels.size() + "size");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (songModels.size() > 0 && lastKnownIndex == -1) {
                        lastKnownIndex = 0;
                        initializePlayer(Uri.parse(songModels.get(0).getSongPath()));
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.connectionlost), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);


        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        checkConnectivity();
        checkAuth();


        start = "0";


    }

    private void checkConnectivity() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else {
                connected = false;

            }
        }
    }

    private void getMaximumId() {
        DatabaseReference ref2 = database.getReference("id");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getValue(String.class);
//                Log.v(TAG, end);
                shuffleModeInFirebase(database);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void checkAuth() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            database = FirebaseDatabase.getInstance();
            getMaximumId();
//            uploadFileToFirebase(uri);
        } else {
            mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    database = FirebaseDatabase.getInstance();
                    getMaximumId();

//                    uploadFileToFirebase(uri);

                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "signInAnonymously:FAILURE", exception);
                }
            });
        }
    }

    private void initializePlayer(Uri uri) {

        if (lastKnownIndex != -1) {
            Utils.saveObjectInPreference(this, "SongModel", songModels.get(lastKnownIndex));
        }
        if (uri != null) {
            SimpleAppWidgetProvider.sendRefreshBroadcast(this);
//            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
//                    new DefaultRenderersFactory(getActivity().getApplicationContext()),
//                    new DefaultTrackSelector(), new DefaultLoadControl());

            mediaSource = buildMediaSource(uri);
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(this.getApplicationContext()),
                        new DefaultTrackSelector(), new DefaultLoadControl());

            }
            if (bundle != null)
                isPlayWhenReady = bundle.getBoolean("playstate");
            player.setPlayWhenReady(isPlayWhenReady);
            player.prepare(mediaSource, true, false);
            player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case ExoPlayer.STATE_BUFFERING:
                            break;
                        case ExoPlayer.STATE_ENDED:
                            //do what you want
                            if (songModels != null) {
                                int jj = (int) (Math.random() * songModels.size() - 1);
                                lastKnownIndex = jj;
                                mediaSource = buildMediaSource(Uri.parse(songModels.get(jj).getSongPath()));
                                if (bundle != null)
                                    isPlayWhenReady = bundle.getBoolean("playstate");
                                player.setPlayWhenReady(isPlayWhenReady);
                                player.prepare(mediaSource, true, false);
                            }
                            break;
                        case ExoPlayer.STATE_IDLE:
                            break;
                        case ExoPlayer.STATE_READY:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });


            video_view.setPlayer(player);
            if (bundle != null) {
                player.seekTo(bundle.getLong("timer"));
            }

        }

//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }
    /**
     *
     * All The Commented Methods are for the Uploading to be completed
     *
     *
     * @param uri
     */
//    void checkPermissions() {
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            // No explanation needed, we can request the permission.
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_CONTACTS},
//                    READ_EXTERNAL_STORAGE_PERMISSION_CODE);
//
//            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//            // app-defined int constant. The callback method gets the
//            // result of the request.
//
//        } else
//            showFileChooser();
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case READ_EXTERNAL_STORAGE_PERMISSION_CODE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    showFileChooser();
//                } else {
//                    Toast.makeText(this, "Please Allow Reading From External Storage For Uploading", Toast.LENGTH_SHORT).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        try {
//            startActivityForResult(
//                    Intent.createChooser(intent, "Select a File to Upload"),
//                    FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * onActivityResult was only used to choose a mp3 file and upload it to database
     *
     * @param
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case FILE_SELECT_CODE:
//                if (resultCode == RESULT_OK) {
//                    // Get the Uri of the selected file
//                    Uri uri = data.getData();
//                    Log.d(TAG, "File Uri: " + uri.toString());
//
//                    try {
////                        s = data.getData().getPath();
////                        Log.d(TAG, "File Path :  " + s);
//                        mmr = new MediaMetadataRetriever();
//                        mmr.setDataSource(MainActivity.this, uri);
//                        albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//                        METADATA_KEY_ARTIST = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                        METADATA_KEY_AUTHOR = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
//                        METADATA_KEY_DURATION = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                        METADATA_KEY_GENRE = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//                        METADATA_KEY_TITLE = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                        Log.v(TAG, "albumName ---> " + albumName);
//                    } catch (Exception e) {
////                        album_art.setBackgroundColor(Color.GRAY);
//                        albumName = ("Unknown Album");
//                        METADATA_KEY_ARTIST = ("Unknown Artist");
//                        METADATA_KEY_AUTHOR = ("Unknown Author");
//                        METADATA_KEY_DURATION = ("Unknown Duration");
//                        METADATA_KEY_GENRE = ("Unknown Genre");
//                        METADATA_KEY_TITLE = ("Unknown Title");
//
//                    }
//
//                    checkAuth(uri);
//                    // Get the path
//                    String path = null;
//                    try {
//                        path = FileUtils.getPath(this, uri);
//                        Log.d(TAG, "File Path: " + path);
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//
//                    // Get the file instance
//                    // File file = new File(path);
//                    // Initiate the upload
//                }
////                mUploadButton.setClickable(true);
//                progressBar.setVisibility(View.GONE);
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    /**
     * uploadFileToFirebase as well as firebaseupdate methods are both for the uploading feature which
     * i used to upload and save data in firebase
     *
     * @param uri
     */
//    private void uploadFileToFirebase(Uri uri) {
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        // Create a storage reference from our app
//        StorageReference storageRef = storage.getReference();
//
//        // File or Blob   //uri
//
//        // Create the file metadata
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType("audio/mpeg")
//                .build();
//        // Upload file and metadata to the path 'images/mountains.jpg'
//        UploadTask uploadTask = storageRef.child("songs/" + uri.getLastPathSegment()).putFile(uri, metadata);
//
//        // Listen for state changes, errors, and completion of the upload.
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                System.out.println("Upload is " + progress + "% done");
//                Log.d(TAG, "taskSnapshot -----> " + "Upload is " + progress + "% done" + " <----------");
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                System.out.println("Upload is paused");
//                Log.d(TAG, "taskSnapshot -----> " + "Upload is paused" + " <----------");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                Log.d(TAG, "exception -----> " + exception.getMessage() + " <----------");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // Handle successful uploads on complete
//                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
//                initializePlayer(downloadUrl);
//
//                SongModel songModel = new SongModel();
//                if(METADATA_KEY_ARTIST!=null)
//                songModel.setSongArtist(METADATA_KEY_ARTIST);
//                else
//                    songModel.setSongArtist("Unknown");
//                if(METADATA_KEY_TITLE!=null)
//                songModel.setSongName(METADATA_KEY_TITLE);
//                else
//                    songModel.setSongName("Unknown");
//                if(METADATA_KEY_AUTHOR!=null)
//                songModel.setSongAuthor(METADATA_KEY_AUTHOR);
//                else
//                    songModel.setSongAuthor("Unknown");
//                if(METADATA_KEY_DURATION!=null)
//                songModel.setSongDuration(METADATA_KEY_DURATION);
//                else
//                    songModel.setSongDuration("Unknown");
//                if(METADATA_KEY_GENRE!=null)
//                songModel.setSongGenre(METADATA_KEY_GENRE);
//                else
//                    songModel.setSongGenre("Unknown");
//
//                songModel.setSongPath(String.valueOf(downloadUrl));
//                firebaseDataBaseUpdate(songModel);
//
//            }
//
//
//        });
//
//    }

//    private void firebaseDataBaseUpdate(SongModel songModel) {
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//
//        database.child("id").setValue(String.valueOf(i));
//
//        songModel.setSongId(String.valueOf(i));
//        database.child("songs").child(String.valueOf(i)).setValue(songModel);
//        i++;
//    }


    // This Part was tested however i want to keep the player running in the background
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.release();
//        }
//    }
//
//        @Override
//    public void onStop() {
//        super.onStop();
//        if (player != null) {
//            player.release();
//        }
//    }

}

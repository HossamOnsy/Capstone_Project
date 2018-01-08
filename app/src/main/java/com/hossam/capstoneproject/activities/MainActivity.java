package com.hossam.capstoneproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hossam.capstoneproject.R;
import com.hossam.capstoneproject.models.SongModel;
import com.hossam.capstoneproject.utils.FileUtils;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.upload)
    Button mUploadButton;
    @BindView(R.id.video_view)
    SimpleExoPlayerView video_view;

    SimpleExoPlayer player;

    Bundle bundle = null;
    boolean isPlayWhenReady = true;

    Unbinder unbinder;

    @OnClick({R.id.upload})
    void View(View view) {
        switch (view.getId()) {
            case R.id.upload: {
                mUploadButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                checkPermissions();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
    }

    void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_EXTERNAL_STORAGE_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else
            showFileChooser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    showFileChooser();
                } else {
                    Toast.makeText(this, "Please Allow Reading From External Storage For Uploading", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());

                    try {
                        String s = data.getData().getPath();
                        Log.d(TAG, "File Path :  " + s);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    checkAuth(uri);
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, uri);
                        Log.d(TAG, "File Path: " + path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                mUploadButton.setClickable(true);
                progressBar.setVisibility(View.GONE);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkAuth(final Uri uri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uploadFileToFirebase(uri);
        } else {
            mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    uploadFileToFirebase(uri);
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "signInAnonymously:FAILURE", exception);
                }
            });
        }
    }


    private void uploadFileToFirebase(Uri uri) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // File or Blob   //uri

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();
        // Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child("songs/" + uri.getLastPathSegment()).putFile(uri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
                Log.d(TAG, "taskSnapshot -----> " + "Upload is " + progress + "% done" + " <----------");
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
                Log.d(TAG, "taskSnapshot -----> " + "Upload is paused" + " <----------");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "exception -----> " + exception.getMessage() + " <----------");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                initializePlayer(downloadUrl);
                Log.d(TAG, "taskSnapshot -----> " + downloadUrl + " <----------");
                Log.d(TAG, "taskSnapshot -----> " + taskSnapshot.getMetadata().getName() + " <----------");
                Log.d(TAG, "taskSnapshot -----> " + taskSnapshot.getMetadata().getContentType() + " <----------");
                Log.d(TAG, "taskSnapshot -----> " + taskSnapshot.getMetadata().getPath() + " <----------");
                Log.d(TAG, "taskSnapshot -----> " + taskSnapshot.getMetadata().getCreationTimeMillis() + " <----------");
                SongModel songModel = new SongModel();
                songModel.setSongName(taskSnapshot.getMetadata().getName());
                songModel.setSongContentType(taskSnapshot.getMetadata().getContentType());
                songModel.setSongCreationTimeMilis(String.valueOf(taskSnapshot.getMetadata().getCreationTimeMillis()));
                songModel.setSongPath(taskSnapshot.getMetadata().getPath());
                firebaseDataBaseUpdate(songModel);

            }


        });

    }

    private void firebaseDataBaseUpdate(SongModel songModel) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.child("songs").push().getKey();
        songModel.setSongId(id);
        database.child("songs").child(id).setValue(songModel);

    }

    private void initializePlayer(Uri uri) {
        if (uri != null) {
//            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
//                    new DefaultRenderersFactory(getActivity().getApplicationContext()),
//                    new DefaultTrackSelector(), new DefaultLoadControl());

            MediaSource mediaSource = buildMediaSource(uri);
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(this.getApplicationContext()),
                        new DefaultTrackSelector(), new DefaultLoadControl());

            }
            if (bundle != null)
                isPlayWhenReady = bundle.getBoolean("playstate");
            player.setPlayWhenReady(isPlayWhenReady);
            player.prepare(mediaSource, true, false);

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
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.release();
//        }
//    }

    //    @Override
//    public void onStop() {
//        super.onStop();
//        if (player != null) {
//            player.release();
//        }
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }
}

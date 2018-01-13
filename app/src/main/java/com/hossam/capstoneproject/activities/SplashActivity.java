package com.hossam.capstoneproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hossam.capstoneproject.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity implements RecognitionListener {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    SpeechRecognizer speech;
    @BindView(R.id.intriguing)
    SwitchCompat intriguing;
    @BindView(R.id.say_ok_random_text_view)
    TextView say_ok_random_text_view;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;
    @BindView(R.id.logo)
    ImageView logo;


    Unbinder unbinder;

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                promptSpeechInput();
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        unbinder = ButterKnife.bind(this);
        intriguing.setVisibility(View.GONE);
        intriguing.setAlpha(0.0f);
         Animation animation1 = AnimationUtils.loadAnimation(this,
         R.anim.slide);
         animation1.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 intriguing.setVisibility(View.VISIBLE);
                 intriguing.animate().setDuration(1000).alpha(1.0f);
                 logo.animate().setDuration(1000).alpha(0.0f);
                 logo.setVisibility(View.GONE);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
        logo.startAnimation(animation1);
        // Animation animation1 = AnimationUtils.loadAnimation(this,
        // R.anim.myanimation);
        // animation1.setAnimationListener(this);
        // animatedView1.startAnimation(animation1);
    }

    @OnClick({R.id.intriguing})
    void View(View view) {
        switch (view.getId()) {
            case R.id.intriguing: {
                say_ok_random_text_view.setVisibility(View.VISIBLE);
                say_ok_random_text_view.animate().alpha(1f);
                intriguing.animate().alpha(0f);
                speech = SpeechRecognizer.createSpeechRecognizer(this);
                speech.setRecognitionListener(this);
                promptSpeechInput();
            }
            break;

        }
    }

    private void promptSpeechInput() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt));
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
        Intent recognizerIntent;
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        progressBar1.setVisibility(View.VISIBLE);
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    for (String s : result) {
//                        Log.d(TAG,"result ------->  "  + s + " <------- result");
                        if (s.toLowerCase().contains("random")) {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                    }
                }
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");

//        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(TAG, "FAILED " + errorMessage);
//        returnedText.setText(errorMessage);
//        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        Boolean found=false;
        if (matches != null) {
            for (String result : matches) {
                Log.d(TAG, "result ------->  " + result + " <------- result");
                text += result + "\n";
                if (result.toLowerCase().contains("random")) {
                    progressBar1.setVisibility(View.GONE);
                    speech.stopListening();
                    intriguing.setChecked(false);
                    intriguing.animate().alpha(1f);
                    say_ok_random_text_view.setVisibility(View.INVISIBLE);
                    intriguing.setVisibility(View.VISIBLE);
                    found=true;
                    startActivity(new Intent(this, MainActivity.class));
                    break;
                }
            }

        }
        if(!found){
            promptSpeechInput();

        }

//        returnedText.setText(text);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged: " + rmsdB);
//        progress.setProgress((int) rmsdB);
    }
}

package com.eddy.app.alarmself.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.VideoView;

import com.androdocs.httprequest.HttpRequest;


import com.eddy.app.alarmself.R;
import com.eddy.app.alarmself.db.DatabaseManager;
import com.eddy.app.alarmself.util.Parcelables;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class AlarmAlertActivity extends Activity {

    private static final String TAG = "AlarmAlertActivity";
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean alarmActive;
    private ClickListener clickListener;
    TextView temperature;
    TabHost th;
    VideoView video;
    String CITY = "London,uk";
    String API = "90c8c7a8d3996d110775fce0986b381a";
    private int brightness;
    private ArrayList<String> videoArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm_alert);

        DatabaseManager.init(getBaseContext());

        clickListener = new ClickListener();
        findViewById(R.id.stopImage).setOnClickListener(clickListener);

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            alarm = Parcelables.toParcelableAlarm(bundle.getByteArray(Alarm.TAG));
        }

        if (alarm == null) {
            Log.d(TAG, "Alarm is null!");
            return;
        }

        this.setTitle(alarm.getName());

        final TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        final CallStateListener callStateListener = new CallStateListener();

        telephonyManager.listen(callStateListener, CallStateListener.LISTEN_CALL_STATE);

        startAlarm();
        temperature = (TextView) findViewById(R.id.temperatureText);

        new weatherTask().execute();
        showVideo().start();

    }

    private void startAlarm() {
        if (alarm != null && !alarm.getTonePath().isEmpty()) {
            Log.d(TAG, "startAlarm(): " + alarm.getAlarmTimeStringParcelable());
            mediaPlayer = new MediaPlayer();

            if (alarm.shouldVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 200, 200, 200};
                vibrator.vibrate(pattern, 0);
            }

            try {
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this, Uri.parse(alarm.getTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
            } finally {
                mediaPlayer.release();
                alarmActive = false;
            }
        }
    }

    private void stopAlarm() {
        if (alarm != null) {
            Log.d(TAG, "stop alarm");
            alarm.setActive(false);
            DatabaseManager.update(alarm);

            try {
                vibrator.cancel();
            } catch (Exception e) {

            }
            try {
                mediaPlayer.stop();
            } catch (Exception e) {

            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, intent.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;
    }

    @Override
    public void onBackPressed() {
        if (!alarmActive) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!alarmActive || alarm == null) {
                return;
            }

            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            stopAlarm();
            finish();
        }
    }

    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(getClass().getSimpleName(), "Incoming call: "
                            + incomingNumber);
                    try {
                        mediaPlayer.pause();
                    } catch (Exception e) {

                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(getClass().getSimpleName(), "Call State Idle");
                    try {
                        mediaPlayer.start();
                    } catch (Exception e) {

                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class weatherTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&&units=metric&appid=" + API);
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                //* Populating extracted data into our views *//*
                //addressTxt.setText(address);
                //updated_atTxt.setText(updatedAtText);
                //statusTxt.setText(weatherDescription.toUpperCase());
                temperature.setText(temp);
                //temp_minTxt.setText(tempMin);
                //temp_maxTxt.setText(tempMax);
                //sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                //sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                //windTxt.setText(windSpeed);
                //pressureTxt.setText(pressure);
                //humidityTxt.setText(humidity);

                //* Views populated, Hiding the loader, Showing the main design *//*
                //findViewById(R.id.loader).setVisibility(View.GONE);
                //findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                //findViewById(R.id.loader).setVisibility(View.GONE);
                //findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

    public VideoView showVideo() {
        videoArray = new ArrayList<>();
        videoArray.add("android.resource://" + getPackageName() + "/" + (String.valueOf(getResources().getIdentifier("rain1",
                "raw", getPackageName()))));
        videoArray.add("android.resource://" + getPackageName() + "/" + (String.valueOf(getResources().getIdentifier("waves1",
                "raw", getPackageName()))));
        videoArray.add("android.resource://" + getPackageName() + "/" + (String.valueOf(getResources().getIdentifier("rain1",
                "raw", getPackageName()))));


        // videoArray.add("android.resource://" + getPackageName() + "/" + R.raw.s1);
        int randomNumber = (int) (Math.random() * videoArray.size() + 0);


        video = (VideoView) findViewById(R.id.videoView);

        String path1 = videoArray.get(randomNumber);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(video);
        mc.setMediaPlayer(video);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        Uri uri = Uri.parse(path1);
        video.setMediaController(mc);
        video.setVideoURI(uri);
        return video;
    }
}


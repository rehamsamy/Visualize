package android.example.com.visualizerpreferences;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.example.com.visualizerpreferences.AudioVisuals.AudioInputReader;
import android.example.com.visualizerpreferences.AudioVisuals.VisualizerView;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeListener;

public class VisualizerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        mVisualizerView = (VisualizerView) findViewById(R.id.activity_visualizer);
        defaultSetup();
        setupPermissions();

    }

    private void defaultSetup() {

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.show_bass),getResources().getBoolean(R.bool.show_bass_default)));
           Boolean y=sharedPreferences.getBoolean(getString(R.string.show_mid),getResources().getBoolean(R.bool.show_mid_default));
        mVisualizerView.setShowMid(y);
           Boolean z=sharedPreferences.getBoolean(getString(R.string.show_triple),getResources().getBoolean(R.bool.show_treple_default));
       // mVisualizerView.setShowBass(true);

        String color=sharedPreferences.getString(getString(R.string.pref_list_color),getString(R.string.pref_color_green_value));

        String scale=sharedPreferences.getString(getString(R.string.scale_key),getString(R.string.scale_default_value));
        Float scale2=Float.parseFloat(scale);

        mVisualizerView.setShowTreble(z);
        mVisualizerView.setMinSizeScale(scale2);
        mVisualizerView.setColor(color);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("key1",getResources().getBoolean(R.bool.show_bass_default));
    }


    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.setting){
            startActivity(new Intent(this,SettingActivity.class));

        }

        return  true;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.show_bass))){
            boolean x=sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.show_bass_default));
            mVisualizerView.setShowBass(x);
        }
        else if(key.equals(getString(R.string.show_mid))){
            boolean y=sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.show_mid_default));
            mVisualizerView.setShowMid(y);
        }
       else if(key.equals(getString(R.string.show_triple))){
            boolean z=sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.show_treple_default));
            mVisualizerView.setShowTreble(z);
        }
        else if(key.equals(getString(R.string.pref_list_color))){
            String colr=sharedPreferences.getString(key,getString(R.string.pref_color_green_value));
            mVisualizerView.setColor(colr);
        }
        else if(key.equals(getString(R.string.scale_key))){
            String scale=sharedPreferences.getString(getString(R.string.scale_key),getString(R.string.scale_default_value));
            Float scale2=Float.parseFloat(scale);
            mVisualizerView.setMinSizeScale(scale2);
        }


    }






}
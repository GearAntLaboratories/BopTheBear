package com.grant.bopthebear;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;


public class OptionsActivity extends Activity {

    int soundOn = 1;
    private CheckBox soundCB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 178, 102));
        soundCB = (CheckBox) findViewById(R.id.cbSound);

        //Change checkbox to appropriate value based on saved preferences
        SharedPreferences soundPref = PreferenceManager.getDefaultSharedPreferences(this);
        soundOn = soundPref.getInt("sound", 1);
        //
        if (soundOn == 1) {
            soundCB.setChecked(true);
        } else
            soundCB.setChecked(false);
    }

    //opens main menu, saves sound preference
    public void openMain(View view) {
        SharedPreferences soundPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (soundCB.isChecked()) {
            soundPref.edit().putInt("sound", 1).commit();
        } else
            soundPref.edit().putInt("sound", 0).commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

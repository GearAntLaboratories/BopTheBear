package com.grant.bopthebear;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class GameActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_layout);

        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sp.edit();
        prefEditor.putInt("score", 0);
        prefEditor.commit();


    }
}

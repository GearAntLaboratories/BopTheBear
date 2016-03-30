package com.grant.bopthebear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ScoresActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 178, 102));

        //Restore user info; highScore SharedPreferences
        //Example of shared preferences, persists between app closes and activity changes
        SharedPreferences userInfo = PreferenceManager.getDefaultSharedPreferences(this);
        int highScore = userInfo.getInt("high_score", 0);

        //Example of Private Preferences
        //Keeps count of number of high score resets
        SharedPreferences clearCounter = getPreferences(MODE_PRIVATE);
        int clearCount = clearCounter.getInt("clear_Count", 0);


        //sets score found to display in textview
        TextView score = (TextView) findViewById(R.id.tvScore);
        score.setText(highScore + "");

        TextView clearResetNum = (TextView) findViewById(R.id.tvResetCount);
        clearResetNum.setText("" + clearCount);


    }


//------


    //opens main menu
    public void openMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //clears sharedpreferences(high score), updates private preferences (clear count)
    public void clear(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                switch (whichButton) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getApplicationContext(), "Data Cleared!", Toast.LENGTH_SHORT).show();
                        SharedPreferences highScore = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = highScore.edit();
                        editor.putInt("high_score", 0);
                        editor.commit();
                        TextView score = (TextView) findViewById(R.id.tvScore);
                        score.setText("0");
                        SharedPreferences clearCounter = getPreferences(MODE_PRIVATE);
                        int clearCount = clearCounter.getInt("clear_Count", 0);
                        clearCount++;
                        editor = clearCounter.edit();
                        editor.putInt("clear_Count", clearCount);
                        editor.commit();
                        TextView clearResetNum = (TextView) findViewById(R.id.tvResetCount);
                        clearResetNum.setText("" + clearCount);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), "Good decision!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //Makes sure user wants to delete score
        new AlertDialog.Builder(this)
                .setTitle("Bop the Bear")
                .setMessage("Do you really want to clear your high score?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}

package com.grant.bopthebear;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

public class TitleView extends View {

    //Variable declarations
    private Bitmap titleGraphic;
    private Bitmap playButton;
    private Bitmap optionsButton;
    private Bitmap scoresButton;
    private Bitmap playButtonDown;
    private Bitmap optionsButtonDown;
    private Bitmap scoresButtonDown;
    private int screenWidth;
    private int buttonWidth;
    private int buttonHeight;
    boolean playButtonPressed;
    boolean optionButtonPressed;
    boolean scoresButtonPressed;
    private Context myContext;


    public TitleView(Context context) {
        super(context);
        myContext = context;
        //load up all of the button pictures and graphics for title screen

        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);
        playButton = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
        optionsButton = BitmapFactory.decodeResource(getResources(), R.drawable.options_button);
        scoresButton = BitmapFactory.decodeResource(getResources(), R.drawable.high_scores_button);
        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
        optionsButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.options_button_down);
        scoresButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.high_scores_button_down);
        buttonWidth = playButton.getWidth();
        buttonHeight = playButton.getHeight();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenWidth = w;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        //colors the entire background
        canvas.drawColor(Color.rgb(239, 224, 185));
        //draws all graphics for title screen, horizontally centered
        canvas.drawBitmap(titleGraphic, ((screenWidth - titleGraphic.getWidth()) / 2), 0, null);

        //draws down buttons if they are pressed, otherwise regular buttons
        if (playButtonPressed) {
            canvas.drawBitmap(playButtonDown, ((screenWidth - playButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75), null);
        } else
            canvas.drawBitmap(playButton, ((screenWidth - playButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75), null);

        if (optionButtonPressed) {
            canvas.drawBitmap(optionsButtonDown, ((screenWidth - optionsButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 3), null);
        } else
            canvas.drawBitmap(optionsButton, ((screenWidth - optionsButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 3), null);

        if (scoresButtonPressed) {
            canvas.drawBitmap(scoresButtonDown, ((screenWidth - scoresButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 5), null);
        } else
            canvas.drawBitmap(scoresButton, ((screenWidth - scoresButton.getWidth()) / 2), (float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 5), null);

    }

    public boolean onTouchEvent(MotionEvent event) {

        //checks for touch event on areas where the buttons are placed
        //code then starts appropriate activities for each button
        int eventAction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if (
                        X > (screenWidth - buttonWidth) / 2 &&
                                X < (screenWidth - buttonWidth) / 2 + buttonWidth &&
                                Y > ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75)) &&
                                Y < ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75) + buttonHeight)
                        ) {
                    playButtonPressed = true;
                } else if (
                        X > (screenWidth - buttonWidth) / 2 &&
                                X < (screenWidth - buttonWidth) / 2 + buttonWidth &&
                                Y > ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 3)) &&
                                Y < ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 3) + buttonHeight)
                        ) {
                    optionButtonPressed = true;
                } else if (
                        X > (screenWidth - buttonWidth) / 2 &&
                                X < (screenWidth - buttonWidth) / 2 + buttonWidth &&
                                Y > ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 5)) &&
                                Y < ((float) (titleGraphic.getHeight() + playButton.getHeight() * .75 * 5) + buttonHeight)
                        ) {
                    scoresButtonPressed = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (playButtonPressed) {
                    Intent gameIntent = new Intent(myContext, GameActivity.class);
                    myContext.startActivity(gameIntent);
                }

                if (optionButtonPressed) {
                    Intent optionsIntent = new Intent(myContext, OptionsActivity.class);
                    myContext.startActivity(optionsIntent);
                }

                if (scoresButtonPressed) {
                    Intent scoresIntent = new Intent(myContext, ScoresActivity.class);
                    myContext.startActivity(scoresIntent);
                }

                optionButtonPressed = false;
                scoresButtonPressed = false;
                playButtonPressed = false;
                break;


        }

        invalidate();
        return true;
    }


}

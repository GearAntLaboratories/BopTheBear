package com.grant.bopthebear;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.preference.PreferenceManager;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    //variable declarations
    private SurfaceHolder mySurfaceHolder;
    private Bitmap backgroundImg;
    private Bitmap gameOverScreen;
    private boolean running = false;
    private BopTheBearThread thread;
    private Paint blackPaint;
    private int backgroundOrigW;
    private int backgroundOrigH;
    private float scaleW;
    private float scaleH;
    private float drawScaleW;
    private float drawScaleH;
    private Bitmap mask;
    private Bitmap bear;
    private Bitmap bop;
    private int bopped = 0;
    private int missed = 0;
    private boolean gameOver = false;
    private boolean bopping = false;

    private int touchX, touchY;

    //sound variables
    private static SoundPool sounds;
    private static int bopSound;
    private static int missSound;
    public boolean soundOn = true;


    //Positions of each bear and masks that hide the bears
    private int mask1x, mask2x, mask3x, mask4x, mask5x, mask6x, mask7x;
    private int mask1y, mask2y, mask3y, mask4y, mask5y, mask6y, mask7y;

    private int bear1x, bear2x, bear3x, bear4x, bear5x, bear6x, bear7x;
    private int bear1y, bear2y, bear3y, bear4y, bear5y, bear6y, bear7y;


    //Animation variables
    private int activeBear = 0;
    private boolean bearSinking = true;
    private boolean bearRising = false;
    private int bearSpeed = 5;
    private boolean bearBopped = false;

    SharedPreferences gamePrefs = PreferenceManager.getDefaultSharedPreferences(getContext());


    //constructor, gets surface holder, assigns callback, creates instance of thread
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //paint for score text
        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blackPaint.setTextSize(80);


        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //loading sounds
        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        bopSound = sounds.load(context, R.raw.bop, 1);
        missSound = sounds.load(context, R.raw.miss, 1);

        //checking if sound is on or off
        int soundPref = gamePrefs.getInt("sound", 1);
        if (soundPref == 0) {
            soundOn = false;
        }


        thread = new BopTheBearThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true);

    }

    //main thread
    class BopTheBearThread extends Thread {

        public BopTheBearThread(SurfaceHolder surfaceHolder, Context context,
                                Handler handler) {
            mySurfaceHolder = surfaceHolder;
            //load images
            backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_play_background_new);
            backgroundOrigH = backgroundImg.getHeight();
            backgroundOrigW = backgroundImg.getWidth();
            gameOverScreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over_screen);
            bear = BitmapFactory.decodeResource(context.getResources(), R.drawable.bear_image);
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.cave_mask);
            bop = BitmapFactory.decodeResource(context.getResources(), R.drawable.bop);
            pickActiveBear();


        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        if (!gameOver) {
                            animateBears();
                        }
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        //draws canvas, bears, masks
        private void draw(Canvas canvas) {
            try {
                canvas.drawBitmap(backgroundImg, 0, 0, null);

                canvas.drawBitmap(bear, bear1x, bear1y, null);
                canvas.drawBitmap(bear, bear2x, bear2y, null);
                canvas.drawBitmap(bear, bear3x, bear3y, null);
                canvas.drawBitmap(bear, bear4x, bear4y, null);
                canvas.drawBitmap(bear, bear5x, bear5y, null);
                canvas.drawBitmap(bear, bear6x, bear6y, null);
                canvas.drawBitmap(bear, bear7x, bear7y, null);

                canvas.drawBitmap(mask, mask1x, mask1y, null);
                canvas.drawBitmap(mask, mask2x, mask2y, null);
                canvas.drawBitmap(mask, mask3x, mask3y, null);
                canvas.drawBitmap(mask, mask4x, mask4y, null);
                canvas.drawBitmap(mask, mask5x, mask5y, null);
                canvas.drawBitmap(mask, mask6x, mask6y, null);
                canvas.drawBitmap(mask, mask7x, mask7y, null);


                canvas.drawText("Bopped: " + bopped, 0, 60, blackPaint);
                canvas.drawText("Missed: " + missed, 0, 150, blackPaint);

                //draws game over screen on top of everything
                if (gameOver) {
                    canvas.drawBitmap(gameOverScreen, 0, 0, null);
                }

            } catch (Exception e) {
            }

            //draws hit image, centers the middle where touch event happens
            if (bopping) {
                canvas.drawBitmap(bop, touchX - (bop.getWidth() / 2), touchY - (bop.getHeight() / 2), null);
            }
        }

        //get and set width of screen

        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
                scaleW = (float) width / (float) backgroundOrigW;
                scaleH = (float) height / (float) backgroundOrigH;
                mask = Bitmap.createScaledBitmap(mask, (int) (mask.getWidth() * scaleW), (int) (mask.getHeight() * scaleH), true);
                bear = Bitmap.createScaledBitmap(bear, (int) (bear.getWidth() * scaleW), (int) (bear.getHeight() * scaleH), true);
                bop = Bitmap.createScaledBitmap(bop, (int) (bop.getWidth() * scaleW), (int) (bop.getHeight() * scaleH), true);
                gameOverScreen = Bitmap.createScaledBitmap(gameOverScreen, width, height, true);

                //scale factors for draw positions of masks/bears
                drawScaleH = (float) height / 600;
                drawScaleW = (float) width / 800;

                //positions of bears/masks
                mask1x = (int) (0 * drawScaleW);
                mask1y = (int) (280 * drawScaleH);
                bear1x = (int) (mask1x + 33 * drawScaleW);
                bear1y = mask1y;

                mask2x = (int) (113 * drawScaleW);
                mask2y = (int) (430 * drawScaleH);
                bear2x = (int) (mask2x + 33 * drawScaleW);
                bear2y = mask2y;

                mask3x = (int) (200 * drawScaleW);
                mask3y = (int) (210 * drawScaleH);
                bear3x = (int) (mask3x + 33 * drawScaleW);
                bear3y = mask3y;

                mask4x = (int) (332 * drawScaleW);
                mask4y = (int) (355 * drawScaleH);
                bear4x = (int) (mask4x + 33 * drawScaleW);
                bear4y = mask4y;

                mask5x = (int) (464 * drawScaleW);
                mask5y = (int) (210 * drawScaleH);
                bear5x = (int) (mask5x + 33 * drawScaleW);
                bear5y = mask5y;

                mask6x = (int) (553 * drawScaleW);
                mask6y = (int) (430 * drawScaleH);
                bear6x = (int) (mask6x + 33 * drawScaleW);
                bear6y = mask6y;

                mask7x = (int) (666 * drawScaleW);
                mask7y = (int) (280 * drawScaleH);
                bear7x = (int) (mask7x + 33 * drawScaleW);
                bear7y = mask7y;

            }
        }

        public void setRunning(boolean r) {
            running = r;
        }

        //animates active bear, changes directions at bottom of drop, picks another bear if back in cave or bopped
        private void animateBears() {
            if (activeBear == 1) {
                if (bearSinking) {
                    bear1y += bearSpeed;
                } else if (bearRising) {
                    bear1y -= bearSpeed;
                }

                if ((bear1y <= mask1y) || bearBopped) {
                    bear1y = mask1y;
                    pickActiveBear();
                }
                if (bear1y >= mask1y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 2) {
                if (bearSinking) {
                    bear2y += bearSpeed;
                } else if (bearRising) {
                    bear2y -= bearSpeed;
                }

                if ((bear2y <= mask2y) || bearBopped) {
                    bear2y = mask2y;
                    pickActiveBear();
                }
                if (bear2y >= mask2y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 3) {
                if (bearSinking) {
                    bear3y += bearSpeed;
                } else if (bearRising) {
                    bear3y -= bearSpeed;
                }

                if ((bear3y <= mask3y) || bearBopped) {
                    bear3y = mask3y;
                    pickActiveBear();
                }
                if (bear3y >= mask3y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 4) {
                if (bearSinking) {
                    bear4y += bearSpeed;
                } else if (bearRising) {
                    bear4y -= bearSpeed;
                }

                if ((bear4y <= mask4y) || bearBopped) {
                    bear4y = mask4y;
                    pickActiveBear();
                }
                if (bear4y >= mask4y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 5) {
                if (bearSinking) {
                    bear5y += bearSpeed;
                } else if (bearRising) {
                    bear5y -= bearSpeed;
                }

                if ((bear5y <= mask5y) || bearBopped) {
                    bear5y = mask5y;
                    pickActiveBear();
                }
                if (bear5y >= mask5y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 6) {
                if (bearSinking) {
                    bear6y += bearSpeed;
                } else if (bearRising) {
                    bear6y -= bearSpeed;
                }

                if ((bear6y <= mask6y) || bearBopped) {
                    bear6y = mask6y;
                    pickActiveBear();
                }
                if (bear6y >= mask6y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }
            if (activeBear == 7) {
                if (bearSinking) {
                    bear7y += bearSpeed;
                } else if (bearRising) {
                    bear7y -= bearSpeed;
                }

                if ((bear7y <= mask7y) || bearBopped) {
                    bear7y = mask7y;
                    pickActiveBear();
                }
                if (bear7y >= mask7y + 150) {
                    bearSinking = false;
                    bearRising = true;
                }
            }


        }

        private void pickActiveBear() {
            if (!bearBopped && activeBear > 0) {
                missed++;
                if (soundOn) {
                    AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                    float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    sounds.play(missSound, volume, volume, 1, 0, 1);
                }
            }
            activeBear = new Random().nextInt(7) + 1;
            bearRising = false;
            bearSinking = true;
            bearBopped = false;
            //end game scenario = when 5 misses occur
            if (missed > 4) {
                gameOver();
            }


        }

        //detect contact of touch, check if on bear
        private boolean detectContact() {
            boolean contact = false;
            //give = makes it easier to bop bear on touch
            int give = 25;
            if (
                    activeBear == 1 &&
                            touchX >= bear1x - give &&
                            touchX < bear1x + bear.getWidth() + give &&
                            touchY > bear1y &&
                            touchY < bear1y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 2 &&
                            touchX >= bear2x - give &&
                            touchX < bear2x + bear.getWidth() + give &&
                            touchY > bear2y &&
                            touchY < bear2y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 3 &&
                            touchX >= bear3x - give &&
                            touchX < bear3x + bear.getWidth() + give &&
                            touchY > bear3y &&
                            touchY < bear3y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 4 &&
                            touchX >= bear4x - give &&
                            touchX < bear4x + bear.getWidth() + give &&
                            touchY > bear4y &&
                            touchY < bear4y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 5 &&
                            touchX >= bear5x - give &&
                            touchX < bear5x + bear.getWidth() + give &&
                            touchY > bear5y &&
                            touchY < bear5y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 6 &&
                            touchX >= bear6x - give &&
                            touchX < bear6x + bear.getWidth() + give &&
                            touchY > bear6y &&
                            touchY < bear6y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }
            if (
                    activeBear == 7 &&
                            touchX >= bear7x - give &&
                            touchX < bear7x + bear.getWidth() + give &&
                            touchY > bear7y &&
                            touchY < bear7y + bear.getHeight() + give) {
                contact = true;
                bearBopped = true;
            }

            return contact;
        }

        //end game, saves high score if necessary
        public void gameOver() {
            gameOver = true;

            int hScore = gamePrefs.getInt("high_score", 0);
            if (bopped > hScore) {
                SharedPreferences.Editor editor = gamePrefs.edit();
                editor.putInt("high_score", bopped);
                editor.commit();
            }


        }

        //when touch event, detects if there is contact, plays sound, increases score if necessary
        public boolean doTouchEvent(MotionEvent event) {
            synchronized (mySurfaceHolder) {
                int eventaction = event.getAction();
                int X = (int) event.getX();
                int Y = (int) event.getY();

                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        if (!gameOver) {
                            touchX = X;
                            touchY = Y;
                            if (detectContact()) {
                                bopping = true;
                                bopped++;
                                if (soundOn) {
                                    AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                                    float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    sounds.play(bopSound, volume, volume, 1, 0, 1);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        bopping = false;
                        //restarts game if gameover screen is up
                        if (gameOver) {
                            bopped = 0;
                            missed = 0;
                            activeBear = 0;
                            pickActiveBear();
                            gameOver = false;
                        }
                        break;


                }
            }
            return true;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return thread.doTouchEvent(event);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
    }


}
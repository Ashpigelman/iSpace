package com.hit.ispace;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class LevelView extends View {

    //TAG
    private static final String TAG = LevelView.class.getSimpleName();

    private Level level=null;
    private int screenWidth=0;
    private int screenHeight=0;
    private int fingerDownX;
    private boolean gameEnded;
    private long startTime;
    private long checkTime;
    private long km;
    private int speed;
    private ImageButton okDontRemember;
    private CheckBox checkBoxDontRemember;

    protected HandlerThread createElementThread;
    protected HandlerThread animateElementThread;

    protected Handler createElementHandler, animateElementHandler;

    protected boolean sleep;

    public LevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "LevelView created");

        this.gameEnded = false;
        this.speed = 4;
        this.createElementThread = new HandlerThread("create elements thread");
        this.animateElementThread = new HandlerThread("animate elements thread");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //TODO: remove this counter. only for debugging
        int counter=1;

        // first time - initialize the screen width & height
        if (this.screenWidth==0) {
            this.screenWidth = getWidth();
            this.screenHeight = getHeight();
        }

        //draw the avatar on the screen in the right coordinate after touching the screen
        if(level!=null) {
            canvas.drawBitmap(this.level.spaceship.getBitmapSrc(), this.level.spaceship.getLeftTop().getX(), this.level.spaceship.getLeftTop().getY(), null);

            Iterator iter = this.level.elementFactory.getElemList().iterator();
            IElement elem;
            while (iter.hasNext()) {
                elem = (IElement)iter.next();
                try {
                    canvas.drawBitmap(elem.getBitmapSrc(), elem.getLeftTop().getX(), elem.getLeftTop().getY(), null);
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //save the finger coordinate
        double fingerCoordinateX = event.getX();

        // hold the number of pixels the spaceship needs to move (on the X axis)
        int move;

        //make spaceship move
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // save the location where the finger hit first
                this.fingerDownX = (int)fingerCoordinateX;
                break;
            case MotionEvent.ACTION_MOVE:
                // use the saved location to move the spaceship around the canvas
                move = (int)fingerCoordinateX - fingerDownX;
                this.fingerDownX = (int)fingerCoordinateX;

                moveSpaceShip(move);
                invalidate();
                break;
        }

        return true;
    }

    private void moveSpaceShip(int move) {
        //the spaceship moving depends on level type
        switch (level.getLevelType())
        {
            //free style - moving in natural way - left goes left and right goes right
            case CSettings.LevelTypes.FREE_STYLE:
            case CSettings.LevelTypes.FASTER:
                //if spaceship will remain in border of game after moving, make the move
                if (this.level.spaceship.getLeftTop().getX()+move >= 0 && this.level.spaceship.getLeftTop().getX()+move <= getWidth()-level.spaceship.getBitmapSrc().getWidth()) {

                    //set the new coordinate of spaceship
                    this.level.spaceship.getLeftTop().setX(this.level.spaceship.getLeftTop().getX() + move);
                    this.level.spaceship.getRightBottom().setX(this.level.spaceship.getLeftTop().getX() + this.level.spaceship.getSpaceshipWidth());

                    //logs the moves
                    if (move > 0)
                        Log.d(TAG, "spaceship moves " + move + " to right");
                    else if (move < 0)
                        Log.d(TAG, "spaceship moves " + Math.abs(move) + " to Left");

                }
                //force spaceship stay in border of game

                else if (this.level.spaceship.getLeftTop().getX()+move < 0) {
                    //set the new coordinate of spaceship. setting the X coordinate, Y remain the same
                    this.level.spaceship.getLeftTop().setX(0);
                    this.level.spaceship.getRightBottom().setX(CSettings.Dimension.SPACESHIP_SIZE);

                } else if (this.level.spaceship.getLeftTop().getX()+move > getWidth()-this.level.spaceship.getBitmapSrc().getWidth()) {

                    this.level.spaceship.getLeftTop().setX(getWidth()-this.level.spaceship.getBitmapSrc().getWidth());
                    this.level.spaceship.getRightBottom().setX(this.level.spaceship.getLeftTop().getX()+CSettings.Dimension.SPACESHIP_SIZE);

                }
                break;
            //getting sick - moving in the wrong direction - left goes right and vice versa
            case CSettings.LevelTypes.GETTING_SICK:
                //if spaceship will remain in border of game after moving, make the move
                if (this.level.spaceship.getLeftTop().getX() - move >= 0 && this.level.spaceship.getLeftTop().getX() - move <= getWidth()-level.spaceship.getBitmapSrc().getWidth()) {

                    //set the new coordinate of spaceship
                    this.level.spaceship.getLeftTop().setX(this.level.spaceship.getLeftTop().getX() - move);
                    this.level.spaceship.getRightBottom().setX(this.level.spaceship.getLeftTop().getX()+this.level.spaceship.getSpaceshipWidth());

                    //logs the moves
                    if (move > 0)
                        Log.d(TAG, "spaceship moves " + move + " to left");
                    else if (move < 0)
                        Log.d(TAG, "spaceship moves " + Math.abs(move) + " to right");

                }
                //force spaceship stay in border of game

                else if (this.level.spaceship.getLeftTop().getX()+move < 0) {
                    //set the new coordinate of spaceship. setting the X coordinate, Y remain the same
                    this.level.spaceship.getLeftTop().setX(0);
                    this.level.spaceship.getRightBottom().setX(CSettings.Dimension.SPACESHIP_SIZE);

                } else if (this.level.spaceship.getLeftTop().getX()+move > getWidth()-this.level.spaceship.getBitmapSrc().getWidth()) {

                    this.level.spaceship.getLeftTop().setX(getWidth()-this.level.spaceship.getBitmapSrc().getWidth());
                    this.level.spaceship.getRightBottom().setX(this.level.spaceship.getLeftTop().getX()+CSettings.Dimension.SPACESHIP_SIZE);
                }
                break;
            default:
                Log.e(TAG, "no such level type!");
        }
    }

    public void start(Level level) {
        final DatabaseHelper db = new DatabaseHelper(getContext());
        final Dialog dialog = new Dialog(getContext());

        final Level newlevel = level;

        if(db.ifCheckRemember() == 1){
            dialog.setContentView(R.layout.dialog_info_level);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Window window = dialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            okDontRemember = (ImageButton) dialog.findViewById(R.id.btn_done);
            checkBoxDontRemember = (CheckBox) dialog.findViewById(R.id.check_box_dont_remember);
            okDontRemember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBoxDontRemember.isChecked()){
                        db.updateRemember();
                        dialog.dismiss();
                    }
                    else {
                        dialog.dismiss();
                    }
                    LevelView.this.startLevel(newlevel);
                }
            });
            dialog.show();

        } else
            LevelView.this.startLevel(newlevel);
    }

    public void startLevel(Level level) {
        this.level = level;
        final DatabaseHelper db = new DatabaseHelper(getContext());


        Log.d(TAG, "Painting spaceship on the screen");

        String src = db.getSpaceShipSrc();

        Resources res = getContext().getResources();
        int resID = res.getIdentifier(src , "drawable", getContext().getPackageName());

        this.level.spaceship.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),resID),CSettings.Dimension.SPACESHIP_SIZE, CSettings.Dimension.SPACESHIP_SIZE, false));

        //getting the screen size for the canvas and to know where to locate the elements
        this.screenWidth  = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // initialize avatar on the screen when start the level
        Point topLeft = new Point((this.screenWidth / 2) - (this.level.spaceship.getBitmapSrc().getWidth() / 2),this.screenHeight-(CSettings.Dimension.SPACESHIP_SIZE*2));
        Point bottomRight = new Point(topLeft.getX()+CSettings.Dimension.SPACESHIP_SIZE, topLeft.getY()+CSettings.Dimension.SPACESHIP_SIZE);
        this.level.spaceship.setCoordinates(topLeft,bottomRight);
        Log.d(TAG, "GETTING SICK: init at leftTop("+this.level.spaceship.getLeftTop().getX()+","+this.level.spaceship.getLeftTop().getY()+") rightBottom("+this.level.spaceship.getRightBottom().getX()+","+this.level.spaceship.getRightBottom().getY()+")");
        invalidate();

        //start timer
        this.startTime = System.currentTimeMillis();

        //start threads
        createElementThread.start();
        animateElementThread.start();

        this.createElementHandler = new Handler(createElementThread.getLooper());
        this.animateElementHandler = new Handler(animateElementThread.getLooper());

        this.createElementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                LevelView.this.level.elementFactory.createNewElements();

                for (IElement elem : LevelView.this.level.elementFactory.getElemList()) {
                    if (elem.getBitmapSrc() == null) {
                        switch (elem.sayMyName()) {
                            case "Bomb":
                                elem.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_bad_bomb), CSettings.Dimension.ELEMENT_SIZE, CSettings.Dimension.ELEMENT_SIZE, false));
                                break;
                            case "Coin":
                                elem.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_coin), CSettings.Dimension.ELEMENT_SIZE, CSettings.Dimension.ELEMENT_SIZE, false));
                                break;
                            case "GoodBomb":
                                elem.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_good_bomb), CSettings.Dimension.ELEMENT_SIZE, CSettings.Dimension.ELEMENT_SIZE, false));
                                break;
                            case "Rock":
                                elem.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_dead_asteroid), CSettings.Dimension.ELEMENT_SIZE, CSettings.Dimension.ELEMENT_SIZE, false));
                                break;
                            case "SuperRock":
                                elem.setBitmapSrc(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_ufo), CSettings.Dimension.ELEMENT_SIZE, CSettings.Dimension.ELEMENT_SIZE, false));
                                break;
                            case "Space":
                                break;
                        }
                    }
                }

                createElementHandler.postDelayed(this, (LevelView.this.level.getLevelType()==CSettings.LevelTypes.FREE_STYLE?700:350)*LevelView.this.speed);
            }
        }, (LevelView.this.level.getLevelType()==CSettings.LevelTypes.FREE_STYLE?700:350)*LevelView.this.speed);

        this.animateElementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                LevelView.this.checkTime = System.currentTimeMillis();
                LevelView.this.km = (LevelView.this.checkTime-LevelView.this.startTime);

                if (km>=5000 && km<15000) {
                    LevelView.this.speed = 3;
                } else if (km>=15000) {
                    LevelView.this.speed = 2;
                }
                CopyOnWriteArrayList<IElement> templist = LevelView.this.level.elementFactory.getElemList();
                for (IElement elem : templist) {
                    Point newTopLeft = new Point(elem.getLeftTop().getX(),elem.getLeftTop().getY()+(LevelView.this.level.getLevelType()==CSettings.LevelTypes.FREE_STYLE?1:2));
                    Point newBottomRight = new Point(elem.getRightBottom().getX(),elem.getRightBottom().getY()+(LevelView.this.level.getLevelType()==CSettings.LevelTypes.FREE_STYLE?1:2));

                    if (((LevelView.this.level.spaceship.getLeftTop().getX() >= newTopLeft.getX() && LevelView.this.level.spaceship.getLeftTop().getX() <= newBottomRight.getX()) && (LevelView.this.level.spaceship.getLeftTop().getY() >= newTopLeft.getY() && LevelView.this.level.spaceship.getLeftTop().getY() <= newBottomRight.getY()))
                            || ((LevelView.this.level.spaceship.getRightBottom().getX() >= newTopLeft.getX() && LevelView.this.level.spaceship.getRightBottom().getX() <= newBottomRight.getX()) && (LevelView.this.level.spaceship.getRightBottom().getY() >= newTopLeft.getY() && LevelView.this.level.spaceship.getRightBottom().getY() <= newBottomRight.getY()))
                            || ((LevelView.this.level.spaceship.getLeftTop().getX()+CSettings.Dimension.SPACESHIP_SIZE >= newTopLeft.getX() && LevelView.this.level.spaceship.getLeftTop().getX()+CSettings.Dimension.SPACESHIP_SIZE <= newBottomRight.getX()) && (LevelView.this.level.spaceship.getLeftTop().getY() >= newTopLeft.getY() && LevelView.this.level.spaceship.getLeftTop().getY() <= newBottomRight.getY())))
                    {
                        if (elem.getHit()==false) {
                            elem.setHit();
                            LevelView.this.level.elementFactory.setCollectedSize();
                            switch (elem.sayMyName()) {
                                case "Coin":
                                    LevelView.this.level.incrementNumCoinsEarned();
                                    Log.i(TAG, "5 coin added. total of "+LevelView.this.level.getNumCoinsEarned()+" coins");
                                    break;
                                case "Rock":
                                    LevelView.this.setGameEnded(true);
                                    break;
                                case "Bomb":
                                    LevelView.this.level.reduceNumCoins(true);
                                    LevelView.this.setGameEnded(true);
                                    break;
                                case "SuperRock":
                                    Log.i(TAG, "SuperRock hit!!!!!!");
                                    if (!LevelView.this.level.reduceNumCoins(false))
                                        LevelView.this.setGameEnded(true);
                                    break;
                                case "GoodBomb":
                                    Log.i(TAG, "GoodBomb hit!!!!!!");
                                    CopyOnWriteArrayList<IElement> list = LevelView.this.level.elementFactory.getElemList();
                                    int indexToRemove = (int)(Math.random()*list.size());
                                    if (indexToRemove==list.indexOf(elem))
                                        indexToRemove++;
                                    LevelView.this.level.elementFactory.setCollectedSize();
                                    Log.w(TAG,  LevelView.this.level.elementFactory.getElemList().get(indexToRemove).sayMyName() + " destroyed and its id was " + indexToRemove);
                                    LevelView.this.level.elementFactory.getElemList().remove(indexToRemove);
                                    break;
                            }

                            Log.d(TAG, "Hit " + elem.sayMyName());
                            LevelView.this.level.elementFactory.getElemList().remove(elem);
                            postInvalidate();
                        }
                    }
                    elem.setCoordinates(newTopLeft, newBottomRight);

                    // remove elements from data structure
                    if (elem.getLeftTop().getY()>LevelView.this.screenHeight) {
                        Log.i(TAG, "element " + elem.sayMyName() + " has been removed! " + elem.getLeftTop().getY());
                        LevelView.this.level.elementFactory.setCollectedSize();
                        LevelView.this.level.elementFactory.getElemList().remove((IElement)elem);
                        postInvalidate();
                    }
                }
                postInvalidate();
                animateElementHandler.postDelayed(this, LevelView.this.speed);

            }

        }, LevelView.this.level.getLevelType()==CSettings.LevelTypes.FREE_STYLE?700:350);

        Log.i(TAG, "Started playing level #" +level.getLevelType());
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public long getKm()
    {
        return this.km;
    }

}

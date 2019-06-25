package com.hit.ispace;

import android.util.Log;

import java.util.ArrayList;

public class Level {
    private static final String TAG = Level.class.getSimpleName();
    private int levelNumber;
    private double speed;
    private boolean levelEnded;
    private ArrayList<Obstacle> obstacleTypes;

    public Level(int levelNumber) {
        this.levelEnded = false;
        this.levelNumber = levelNumber;
        obstacleTypes = new ArrayList<>();

        switch (this.getLevelNumber()) {
            case 1:
                Log.i(TAG, "Game - Free Style");
                this.speed = Settings.Speed.REGULAR;
                obstacleTypes.add(new Bomb());
                obstacleTypes.add(new Rock());
                obstacleTypes.add(new SuperRock());
                break;
            case 2:
                Log.i(TAG, "Game - Getting Sick");
                obstacleTypes.add(new Rock());
                this.speed = Settings.Speed.REGULAR;
                obstacleTypes.add(new Bomb());
                obstacleTypes.add(new Rock());
                obstacleTypes.add(new SuperRock());
                break;
            case 3:
                Log.i(TAG, "Game - COMING SOON!");
                obstacleTypes.add(new Bomb());
                obstacleTypes.add(new Rock());
                obstacleTypes.add(new SuperRock());
                break;
            default:
                Log.e(TAG, "Level not exists");
        }
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<Obstacle> getObstacleTypes() {
        return obstacleTypes;
    }

    public boolean isLevelEnded() {
        return levelEnded;
    }

    public void setLevelEnded(boolean levelEnded) {
        this.levelEnded = levelEnded;
    }
}
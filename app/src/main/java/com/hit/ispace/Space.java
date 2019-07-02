package com.hit.ispace;

import android.graphics.Bitmap;

public class Space implements IElement {
    private Point topLeft;
    private Point bottomRight;

    private boolean hit;

    @Override
    public void setBitmapSrc(Bitmap bitmap) {
        this.bitmapSrc = bitmap;
    }

    @Override
    public Bitmap getBitmapSrc() {
        return this.bitmapSrc;
    }

    private Bitmap bitmapSrc;

    @Override
    public void setHit() {
        this.hit = true;
    }

    @Override
    public boolean getHit() {
        return this.hit;
    }

    @Override
    public String sayMyName() {
        return "Space";
    }

    public Space(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.hit=false;
    }

    @Override
    public void setCoordinates(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public Point getLeftTop() {
        return this.topLeft;
    }

    @Override
    public Point getRightBottom() {
        return this.bottomRight;
    }
}

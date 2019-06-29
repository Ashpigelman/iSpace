package com.hit.ispace;

public class Space implements IElement {
    Point topLeft;
    Point bottomRight;

    public Space(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
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

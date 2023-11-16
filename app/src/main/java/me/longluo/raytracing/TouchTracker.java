package me.longluo.raytracing;

public class TouchTracker {

    private int pointerID;

    private int sphereID;

    private float x;

    private float y;

    public int getPointerID() {
        return pointerID;
    }

    public void setPointerID(int pointerID) {
        this.pointerID = pointerID;
    }

    public int getSphereID() {
        return sphereID;
    }

    public void setSphereID(int sphereID) {
        this.sphereID = sphereID;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

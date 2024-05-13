package me.longluo.raytracing;

import android.graphics.Bitmap;

public class LibRayTracer {

    // load our native library
    static {
        System.loadLibrary("raytracer");
    }

    private static LibRayTracer instance = null;

    public static LibRayTracer getInstance() {
        if (instance == null) {
            instance = new LibRayTracer();
        }

        return instance;
    }

    public native void initialize(Bitmap input);

    public native void passLightProbe(Bitmap lightProbe);

    public native void passBackground(Bitmap background);

    public native int rayTrace(Bitmap output, long timeElapsed);

    public native void setInterlacingEnabled(boolean enabled);

    public native void setReflectionsEnabled(boolean enabled);

    public native void setLightprobeEnabled(boolean enabled);

    public native int traceTouch(float x, float y);

    public native void moveTouch(float x, float y, int sphereIndex);
}

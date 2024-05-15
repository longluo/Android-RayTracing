package me.longluo.raytracing.util;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final String RAY_TRACING_NAME = "RayTracing";

    public static final String RAY_TRACING_PATH = Environment.getExternalStorageDirectory() + File.separator + RAY_TRACING_NAME;

    public static final int STOP = 0x10000;

    public static final int NEXT = 0x10001;

    public static final int IMAGE_WIDTH = 600;

    public static final int IMAGE_HEIGHT = 400;


}

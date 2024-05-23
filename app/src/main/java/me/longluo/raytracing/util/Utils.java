package me.longluo.raytracing.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import timber.log.Timber;


public final class Utils {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String calculateExpireTime(long nowTime, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nowTime);
        calendar.add(Calendar.DATE, days);

        Date futureTime = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(futureTime);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean createFolderInSdcard(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(Environment.getExternalStorageDirectory(), folderName);

        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        return true;
    }

    public static void createFolder(String filePath) {
        Timber.i("createFolder: %s", filePath);

        if (TextUtils.isEmpty(filePath)) {
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            createFile(filePath);
            return ;
        }

        if (Environment.isExternalStorageManager()) {
            File dir = new File(filePath);

            if (!dir.exists()) {
                boolean isOk = dir.mkdirs();
                Timber.i("%s mkdirs: %b", filePath, isOk);
            } else {
                Timber.i("%s Exists", filePath);
            }
        }
    }

    public static boolean createFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File folder = new File(filePath);

        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        return true;
    }

    public static File createFileInSdcard(String folderName, String fileName) {
        File file = new File(folderName, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write("Hello, World!");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void saveBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap readPPM(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Read PPM file header
        String format = br.readLine(); // This should be "P3"

        if (!"P3".equals(format)) {
            throw new IllegalArgumentException("Unsupported PPM format: " + format);
        }

        // Read image dimensions and max color value
        String line;
        do {
            line = br.readLine();
        } while (line.startsWith("#")); // Ignore comments

        StringTokenizer st = new StringTokenizer(line);
        int width = Integer.parseInt(st.nextToken());
        int height = Integer.parseInt(st.nextToken());

        line = br.readLine();
        int maxColorValue = Integer.parseInt(line);

        // Read the pixel data
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int r, g, b;

        int totalLines = width * height;

        for (int i = 0; i < totalLines; i++) {
            st = new StringTokenizer(br.readLine());

            r = Integer.parseInt(st.nextToken());
            g = Integer.parseInt(st.nextToken());
            b = Integer.parseInt(st.nextToken());

            int color = Color.rgb(
                    (r * 255) / maxColorValue,
                    (g * 255) / maxColorValue,
                    (b * 255) / maxColorValue
            );

            int x = i / height;
            int y = i % height;

            bitmap.setPixel(x, y, color);
        }

        br.close();

        return bitmap;
    }

    public static void saveBitmap(Bitmap bitmap, String filename, Bitmap.CompressFormat format, int quality) throws IOException {
        FileOutputStream out = new FileOutputStream(filename);
        bitmap.compress(format, quality, out);
        out.close();
    }

    public static void convertPPMtoJPEG(String inputFilename, String outputFilename) {
        try {
            Bitmap bitmap = readPPM(inputFilename);
            saveBitmap(bitmap, outputFilename, Bitmap.CompressFormat.JPEG, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

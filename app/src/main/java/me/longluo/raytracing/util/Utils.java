package me.longluo.raytracing.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Utils {

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

}

package me.longluo.raytracing.chapter1;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import me.longluo.raytracing.listener.OnRayTracingListener;
import me.longluo.raytracing.util.Constants;
import me.longluo.raytracing.util.Utils;
import timber.log.Timber;

public class RayTracing1 {

    private int mWidth;

    private int mHeight;

    private int mTotal;

    private String mTitle;

    private String mStorePath;

    private String mPpmFileName;

    private String mImageFileName;

    private Handler mHandler;

    @Nullable
    private OnRayTracingListener mListener;

    public RayTracing1() {
        this(200, 100, "Ray Tracer");
    }

    public RayTracing1(int width, int height, String name) {
        mWidth = width;
        mHeight = height;
        mTitle = name;

        mTotal = width * height;
    }

    public void setListener(OnRayTracingListener listener) {
        mListener = listener;
    }

    public void setStorePath(String path) {
        mStorePath = path;
    }

    public void setProgressHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 设置保存路径及图片名
     *
     * @return 要保存的图片名
     */
    private String initPpmFile() {
        SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");

        String pictureName = mStorePath + "/" + mTitle + "_" + df.format(new Date()) + ".ppm";

        return pictureName;
    }

    private String initPngFile() {
        SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");

        String pictureName = mStorePath + File.separator + mTitle + "_" + df.format(new Date()) + ".jpg";

        return pictureName;
    }

    public void generatePpmImage() {
        mPpmFileName = initPpmFile();

        Timber.d("ppmFileName: %s, total lines: %s", mPpmFileName, mTotal);

        try {
            FileWriter fw = new FileWriter(mPpmFileName);

            fw.write("P3\n" + mWidth + " " + mHeight + "\n255\n");

            int index = 0;

            for (int j = mHeight - 1; j >= 0; j--) {
                for (int i = 0; i < mWidth; i++) {
                    float r = (float) i / (float) mWidth;
                    float g = (float) j / (float) mHeight;
                    float b = 0.2f;

                    index += 1;

                    int ir = (int) (255.59f * r);
                    int ig = (int) (255.59f * g);
                    int ib = (int) (255.59f * b);

                    fw.write(ir + " " + ig + " " + ib + "\n");

                    int currentProgress = (index * 100) / mTotal;

                    Message msg = new Message();
                    msg.what = Constants.NEXT;
                    msg.arg1 = currentProgress;
                    mHandler.sendMessage(msg);
                }
            }

            fw.close();
        } catch (Exception e) {
            if (mListener != null) {
                mListener.onFail(e);
            }

            e.printStackTrace();
        }

        if (mListener != null) {
            mListener.onRenderSuccess(mPpmFileName);
        }

        Timber.d("ppm file OK");
    }

    public void convertPpm2Png() {
        mImageFileName = initPngFile();

        Timber.i("ppm: %s, bmp: %s", mPpmFileName, mImageFileName);

        try {
            Bitmap bitmap = readPPM();
            Utils.saveBitmapToFile(bitmap, new File(mImageFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (mListener != null) {
            mListener.onConvertSuccess(mImageFileName);
        }
    }

    public Bitmap readPPM() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(mPpmFileName));

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

        for (int i = 0; i < mTotal; i++) {
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

            int currentProgress = (i * 100) / mTotal;

            Message msg = new Message();
            msg.what = Constants.NEXT;
            msg.arg1 = currentProgress;
            mHandler.sendMessage(msg);
        }

        br.close();

        Timber.d("Bitmap file OK");

        return bitmap;
    }
}

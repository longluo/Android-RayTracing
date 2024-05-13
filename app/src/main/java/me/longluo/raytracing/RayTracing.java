package me.longluo.raytracing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import me.longluo.raytracing.util.Constants;
import me.longluo.raytracing.util.Utils;
import timber.log.Timber;

public class RayTracing {

    private int width;

    private int height;

    private int mTotal;

    private String name;

    private String mPath;

    private String mPpmFileName;

    private String mImageFileName;

    private Handler mHandler;

    public RayTracing() {
        this(200, 100, "Ray Tracer");
    }

    public RayTracing(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;

        mTotal = width * height;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
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

        String pictureName = mPath + "/" + name + "_" + df.format(new Date()) + ".ppm";

        return pictureName;
    }

    private String initPngFile() {
        SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");

        String pictureName = mPath + File.separator + name + "_" + df.format(new Date()) + ".jpg";

        return pictureName;
    }

    public void generatePpmImage() {
        mPpmFileName = initPpmFile();

        Timber.d("ppmFileName: %s, total lines: %s", mPpmFileName, mTotal);

        try {
            FileWriter fw = new FileWriter(mPpmFileName);

            fw.write("P3\n" + width + " " + height + "\n255\n");

            int index = 0;

            for (int j = height - 1; j >= 0; j--) {
                for (int i = 0; i < width; i++) {
                    float r = (float) i / (float) width;
                    float g = (float) j / (float) height;
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
            Timber.e("Error");
            e.printStackTrace();
        }

        Timber.d("ppm file OK");
    }

    public void convertPpm2Bmp() {
        mImageFileName = initPngFile();

        Timber.i("ppm: %s, bmp: %s", mPpmFileName, mImageFileName);

        try {
            Bitmap bitmap = readPPM();
            Utils.saveBitmapToFile(bitmap, new File(mImageFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
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

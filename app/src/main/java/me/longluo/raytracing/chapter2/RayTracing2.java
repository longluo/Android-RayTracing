package me.longluo.raytracing.chapter2;

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

public class RayTracing2 {

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

    private Vec3 lower_left = new Vec3(-2.0f, -1.0f, -1.0f);

    private Vec3 horizontal = new Vec3(4.0f, 0.0f, 0.0f);

    private Vec3 vertical = new Vec3(0.0f, 2.0f, 0.0f);

    private Vec3 origin = new Vec3(0.0f, 0.0f, 0.0f);

    public RayTracing2(int width, int height, String name) {
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
                    double u = (double) i / (double) mWidth;
                    double v = (double) j / (double) mHeight;

                    Ray ray = new Ray(origin, lower_left.Add(horizontal.Scale(u)).Add(vertical.Scale(v))); //每一条光线
                    Vec3 col = color(ray);    //根据每个像素点上色
                    index += 1;

                    int ir = (int) (255.59f * col.x());
                    int ig = (int) (255.59f * col.y());
                    int ib = (int) (255.59f * col.z());

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

            if (i % 100 == 0) {
                int currentProgress = (i * 100) / mTotal;

                Message msg = new Message();
                msg.what = Constants.NEXT;
                msg.arg1 = currentProgress;
                mHandler.sendMessage(msg);
            }
        }

        br.close();

        Timber.d("Bitmap file OK");

        return bitmap;
    }

    public Vec3 color(Ray r) {
        Vec3 unit_dir = r.direction().normalize();  //单位方向向量
        double t = 0.5f * (unit_dir.y() + 1.0f);     //原本范围为[-1,1]调整为[0,1]
        return new Vec3(1.0f, 1.0f, 1.0f).Scale(1.0f - t).Add(new Vec3(0.5f, 0.7f, 1.0f).Scale(t));
        //返回背景(1.0-t)*vec3(1.0, 1.0, 1.0) + t*vec3(0.5, 0.7, 1.0); 沿着y轴线性插值，返回的颜色介于白色与天蓝色之间
    }
}

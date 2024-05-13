package me.longluo.raytracing;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.longluo.raytracing.util.Utils;
import timber.log.Timber;

public class Display {

    private int width;

    private int height;

    private String name;

    private String ppmFileName;

    private String bmpFileName;

    /**
     * 设置保存路径及图片名
     *
     * @return 要保存的图片名
     */
    private static String initPpmFile() {
        SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");

        File curDirectory = Environment.getExternalStorageDirectory();

        String outputPath = curDirectory.getAbsolutePath();

        String pictureName = outputPath + "/" + "Chapter1_" + df.format(new Date()) + ".ppm";

        return pictureName;
    }

    private static String initPngFile() {
        SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");

        File curDirectory = Environment.getExternalStorageDirectory();

        String outputPath = curDirectory.getAbsolutePath();

        String pictureName = outputPath + "/" +"Chapter1_" + df.format(new Date()) + ".jpg";

        return pictureName;
    }

    public Display() {
        this(200, 100, "Ray Tracer");
    }

    public Display(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public void makeImage() {
        long totalLines = width * height;

        ppmFileName = Display.initPpmFile();

        Timber.d("ppmFileName: %s, total lines: %s", ppmFileName, totalLines);

        try {
            FileWriter fw = new FileWriter(ppmFileName);

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

                    if (index % 100 == 0) {

                    }
                }
            }

            fw.close();
        } catch (Exception e) {
            Timber.e("Error");
            e.printStackTrace();
        }

        bmpFileName = initPngFile();

        Utils.convertPPMtoJPEG(ppmFileName, bmpFileName);

        Timber.i("ppm: %s, bmp: %s", ppmFileName, bmpFileName);
    }
}

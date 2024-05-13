package me.longluo.raytracing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import me.longluo.raytracing.util.Constants;
import me.longluo.raytracing.util.Utils;


public class Chapter1_PpmActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;

    private QMUIRoundButton mPpmBtn;

    private QMUIRoundButton mConvertBtn;
    
    private QMUIProgressBar mRectProgressBar;

    private ImageView mIvResult;

    private RayTracing mRayTracing;

    private Bitmap mResultBitmap;

    private ProgressHandler myHandler = new ProgressHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chapter1);

        initView();
        initData();
    }

    private void initView() {
        mTopBar = findViewById(R.id.topbar);

        mPpmBtn = findViewById(R.id.ppmBtn);

        mConvertBtn = findViewById(R.id.convertBtn);

        mRectProgressBar = findViewById(R.id.rectProgressBar);

        mIvResult = findViewById(R.id.iv_result);
    }

    private void initData() {
        mTopBar.setTitle("Chapter 1 PPM File");

        String path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)
                + File.separator + "Chapter1";

        Utils.createFile(path);

        myHandler.setProgressBar(mRectProgressBar);

        mRayTracing = new RayTracing(600, 400, "Chapter1");

        mRayTracing.setPath(path);

        mRayTracing.setProgressHandler(myHandler);

        mPpmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mRayTracing.generatePpmImage();
                    }
                }).start();
            }
        });

        mConvertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            mResultBitmap = mRayTracing.readPPM();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mIvResult.setImageBitmap(mResultBitmap);
                            }
                        });
                    }
                }).start();
            }
        });

        mRectProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
            @Override
            public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                return value + "/" + maxValue;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class ProgressHandler extends Handler {

        private WeakReference<QMUIProgressBar> weakRectProgressBar;

        void setProgressBar(QMUIProgressBar rectProgressBar) {
            weakRectProgressBar = new WeakReference<>(rectProgressBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.STOP:
                    break;

                case Constants.NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (weakRectProgressBar.get() != null) {
                            weakRectProgressBar.get().setProgress(msg.arg1);
                        }
                    }
            }
        }
    }

}

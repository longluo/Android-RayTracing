package me.longluo.raytracing.chapter12;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;

import me.longluo.droidutils.FileUtils;
import me.longluo.raytracing.R;
import me.longluo.raytracing.listener.OnRayTracingListener;
import me.longluo.raytracing.manager.ThreadPoolManager;
import me.longluo.raytracing.util.Constants;
import me.longluo.raytracing.util.Utils;
import timber.log.Timber;


public class Chapter12_Activity extends AppCompatActivity implements OnRayTracingListener<String> {

    protected static final String CURRENT_MODULE = "Chapter12";

    private static final String STORE_PATH = Constants.RAY_TRACING_PATH + File.separator + CURRENT_MODULE;

    private static final String RENDER_IMAGE_NAME = CURRENT_MODULE + "_" + Constants.IMAGE_WIDTH + "x" + Constants.IMAGE_HEIGHT + ".jpg";

    private QMUITopBarLayout mTopBar;

    private QMUIRoundButton mPpmBtn;

    private QMUIRoundButton mConvertBtn;

    private QMUIProgressBar mRectProgressBar;

    private TextView mTvPpmFile;

    private TextView mTvJpgFile;

    private ImageView mIvResult;

    private RayTracing12 mRayTracing;

    Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Constants.NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (mRectProgressBar != null) {
                            mRectProgressBar.setProgress(msg.arg1);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chapter12);

        initView();
        initData();
    }

    private void initView() {
        mTopBar = findViewById(R.id.topbar);

        mPpmBtn = findViewById(R.id.ppmBtn);

        mConvertBtn = findViewById(R.id.convertBtn);

        mRectProgressBar = findViewById(R.id.rectProgressBar);

        mTvPpmFile = findViewById(R.id.tv_ppm);

        mTvJpgFile = findViewById(R.id.tv_jpg);

        mIvResult = findViewById(R.id.iv_result);
    }

    private void initData() {
        mTopBar.setTitle("Chapter 12  AABB");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)
                + File.separator + CURRENT_MODULE;

        Utils.createFile(path);

//        mRayTracing = new RayTracing12(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT, CURRENT_MODULE);
        mRayTracing = new RayTracing12(1200, 1000, CURRENT_MODULE);

        mRayTracing.setStorePath(path);

        mRayTracing.setProgressHandler(mHandler);

        mRayTracing.setListener(this);

        mPpmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolManager.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        mRayTracing.generatePpmImage();
                    }
                });
            }
        });

        mConvertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolManager.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        mRayTracing.convertPpm2Png();
                    }
                });
            }
        });

        mRectProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
            @Override
            public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                return value + "/" + maxValue;
            }
        });

        String renderImgPath = STORE_PATH + File.separator + RENDER_IMAGE_NAME;
        File file = new File(renderImgPath);

        Timber.i("image path: %s", renderImgPath);

        Glide.with(this).load(file).into(mIvResult);
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

    @Override
    public void onRenderSuccess(String result) {
        Timber.i("onRenderSuccess result %s", result);

        StringBuilder sb = new StringBuilder();
        sb.append(result).append(", size: ").append(FileUtils.getSize(result));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvPpmFile.setText(sb.toString());
                mRectProgressBar.setProgress(0);
            }
        });
    }

    @Override
    public void onConvertSuccess(String result) {
        Timber.i("onConvertSuccess result %s", result);

        StringBuilder sb = new StringBuilder();
        sb.append(result).append(", size: ").append(FileUtils.getSize(result));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvJpgFile.setText(sb.toString());

                File file = new File(result);
                Glide.with(Chapter12_Activity.this).load(file).into(mIvResult);
            }
        });
    }

    @Override
    public void onFail(Exception e) {
        Timber.e("onFail");

    }

}

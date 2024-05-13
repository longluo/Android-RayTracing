package me.longluo.raytracing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.toast.Toaster;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    protected static final int STOP = 0x10000;

    protected static final int NEXT = 0x10001;

    private QMUITopBarLayout mTopBar;

    private QMUIRoundButton mStartBtn;

    private QMUIProgressBar mRectProgressBar;

    private int count;

    private ProgressHandler myHandler = new ProgressHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        requestPermission();

        initView();
        initData();
    }

    private void initView() {
        mTopBar = findViewById(R.id.topbar);

        mStartBtn = findViewById(R.id.startBtn);

        mRectProgressBar = findViewById(R.id.rectProgressBar);
    }

    private void initData() {
        mTopBar.setTitle(R.string.app_name);

        myHandler.setProgressBar(mRectProgressBar);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;

                Display display = new Display(600, 400, "Ray");

                display.makeImage();

                for (int i = 0; i <= 100; i++) {
                    try {
                        count = i + 1;
                        if (i == 5) {
                            Message msg = new Message();
                            msg.what = STOP;
                            myHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = NEXT;
                            msg.arg1 = count;
                            myHandler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    public void toast(CharSequence text) {
        Toaster.show(text);
    }

    private void requestPermission() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        } else {
            doYourWork();
        }
    }

    private boolean checkStoragePermission() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        // Request the permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("已获取权限");
            } else {
                // Permissions are denied
                // Handle the permissions rejection
            }
        }
    }

    private void doYourWork() {
        toast("Done");


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
                case STOP:
                    break;

                case NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (weakRectProgressBar.get() != null) {
                            weakRectProgressBar.get().setProgress(msg.arg1);
                        }
                    }
            }

        }
    }
}

package me.longluo.raytracing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.longluo.raytracing.base.BaseRecyclerAdapter;
import me.longluo.raytracing.base.RecyclerViewHolder;
import me.longluo.raytracing.chapter1.Chapter1_PpmActivity;
import me.longluo.raytracing.chapter10.Chapter10_DefocusActivity;
import me.longluo.raytracing.chapter11.Chapter11_ResultActivity;
import me.longluo.raytracing.chapter12.Chapter12_Activity;
import me.longluo.raytracing.chapter2.Chapter2_RayCameraBgActivity;
import me.longluo.raytracing.chapter3.Chapter3_SphereActivity;
import me.longluo.raytracing.chapter4.Chapter4_SurfaceNormalActivity;
import me.longluo.raytracing.chapter5.Chapter5_AntiAliasingActivity;
import me.longluo.raytracing.chapter6.Chapter6_DiffuseMaterialActivity;
import me.longluo.raytracing.chapter7.Chapter7_MetalActivity;
import me.longluo.raytracing.chapter8.Chapter8_DielecticActivity;
import me.longluo.raytracing.chapter9.Chapter9_CameraActivity;
import me.longluo.raytracing.render.RenderActivity;
import me.longluo.raytracing.util.Utils;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private QMUITopBarLayout mTopBar;

    private QMUIPullRefreshLayout mPullRefreshLayout;

    private RecyclerView mListView;

    private BaseRecyclerAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        initData();

        requestPermission();
    }

    private void initView() {
        mTopBar = findViewById(R.id.topbar);

        mPullRefreshLayout = findViewById(R.id.pull_to_refresh);

        mListView = findViewById(R.id.listview);
    }

    private void initData() {
        mTopBar.setTitle(R.string.app_name);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
        };

        mListView.setLayoutManager(linearLayoutManager);

        mAdapter = new BaseRecyclerAdapter<String>(this, null) {
            @Override
            public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RecyclerViewHolder holder = super.onCreateViewHolder(parent, viewType);
                return holder;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, String item) {
                holder.setText(android.R.id.text1, item);
            }
        };

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Timber.d("Item %d: click", position);

                Toaster.show("Item " + position);

                if (position == 0) {
                    startActivity(new Intent(MainActivity.this, Chapter1_PpmActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(MainActivity.this, Chapter2_RayCameraBgActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(MainActivity.this, Chapter3_SphereActivity.class));
                } else if (position == 3) {
                    startActivity(new Intent(MainActivity.this, Chapter4_SurfaceNormalActivity.class));
                } else if (position == 4) {
                    startActivity(new Intent(MainActivity.this, Chapter5_AntiAliasingActivity.class));
                } else if (position == 5) {
                    startActivity(new Intent(MainActivity.this, Chapter6_DiffuseMaterialActivity.class));
                } else if (position == 6) {
                    startActivity(new Intent(MainActivity.this, Chapter7_MetalActivity.class));
                } else if (position == 7) {
                    startActivity(new Intent(MainActivity.this, Chapter8_DielecticActivity.class));
                } else if (position == 8) {
                    startActivity(new Intent(MainActivity.this, Chapter9_CameraActivity.class));
                } else if (position == 9) {
                    startActivity(new Intent(MainActivity.this, Chapter10_DefocusActivity.class));
                } else if (position == 10) {
                    startActivity(new Intent(MainActivity.this, Chapter11_ResultActivity.class));
                } else if (position == 11) {
                    startActivity(new Intent(MainActivity.this, Chapter12_Activity.class));
                } else if (position == 12) {
                    startActivity(new Intent(MainActivity.this, RenderActivity.class));
                }
            }
        });

        mListView.setAdapter(mAdapter);

        onDataLoaded();

        createExternalFolder();
    }

    private void onDataLoaded() {
        List<String> data = new ArrayList<>();

        data.add("Chapter 1 PPM");
        data.add("Chapter 2 Ray Camera Background");
        data.add("Chapter 3 add a Sphere");
        data.add("Chapter 4 Surface Normals and Multiple Objects");
        data.add("Chapter 5 Anti Aliasing");
        data.add("Chapter 6 Diffuse Materials");
        data.add("Chapter 7 Metal");
        data.add("Chapter 8 Dielectic");
        data.add("Chapter 9 Camear");
        data.add("Chapter 10 Defocus Blur");
        data.add("Chapter 11 Final Result");
        data.add("Chapter 12 AABB");
        data.add("C++ Render RayTracing");

        mAdapter.setData(data);
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
            createExternalFolder();
        }
    }

    private boolean checkStoragePermission() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int managePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);

        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
                && managePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        // Request the permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE},

                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("已获取权限");
                createExternalFolder();
            } else {
                // Permissions are denied
                // Handle the permissions rejection
                openSetting();
            }
        }
    }

    private void openSetting() {
        Timber.i("openSetting");

        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    private void createExternalFolder() {
        Timber.i("createExternalFolder SDK: %d", Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return;
        }

        // 判断是否有使用外部存储的权限
        if (Environment.isExternalStorageManager()) {
            String appPath = Environment.getExternalStorageDirectory() + "/RayTracing/";
            File dir = new File(appPath);

            if (!dir.exists()) {
                boolean isOk = dir.mkdirs();
                Timber.i("%s mkdirs: %b", appPath, isOk);
            } else {
                Timber.i("%s Exists", appPath);
            }
        }

    }
}

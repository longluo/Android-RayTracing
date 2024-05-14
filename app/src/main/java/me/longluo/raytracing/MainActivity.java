package me.longluo.raytracing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import me.longluo.raytracing.base.BaseRecyclerAdapter;
import me.longluo.raytracing.base.RecyclerViewHolder;
import me.longluo.raytracing.chapter1.Chapter1_PpmActivity;
import me.longluo.raytracing.util.Utils;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private QMUITopBarLayout mTopBar;

    private QMUIPullRefreshLayout mPullRefreshLayout;

    private RecyclerView mListView;

    private BaseRecyclerAdapter<String> mAdapter;

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

                Intent intent = new Intent(MainActivity.this, Chapter1_PpmActivity.class);
                startActivity(intent);
            }
        });

        mListView.setAdapter(mAdapter);

        onDataLoaded();

        Utils.createFolderInSdcard(getString(R.string.app_name));
    }

    private void onDataLoaded() {
        List<String> data = new ArrayList<>();

        data.add("Chapter 1");
        data.add("Chapter 2");

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
}

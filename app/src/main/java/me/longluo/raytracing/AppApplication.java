package me.longluo.raytracing;

import android.app.Application;

import com.hjq.toast.Toaster;
import com.hjq.toast.style.WhiteToastStyle;


public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this, new WhiteToastStyle());
    }

}

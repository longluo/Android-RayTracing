package me.longluo.droidutils.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import me.longluo.droidutils.AppLog;
import me.longluo.droidutils.AppLog.T;


public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            // Fix for https://github.com/wordpress-mobile/WordPress-Android/issues/2373
            // Catch IllegalArgumentException which can be fired by the underlying SwipeRefreshLayout.onTouchEvent()
            // method.
            // When android support-v4 fixes it, we'll have to remove that custom layout completely.
            AppLog.e(T.UTILS, e);
            return true;
        }
    }
}

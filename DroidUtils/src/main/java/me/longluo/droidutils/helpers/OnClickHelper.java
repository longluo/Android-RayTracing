package me.longluo.droidutils.helpers;

import android.view.View;


public abstract class OnClickHelper implements View.OnClickListener {

    private static long lastTime;

    private long delay;

    public OnClickHelper(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick(v)) {
            return;
        }

        singleClick(v);
    }

    public boolean onMoreClick(View v) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }

    public abstract void singleClick(View v);
}
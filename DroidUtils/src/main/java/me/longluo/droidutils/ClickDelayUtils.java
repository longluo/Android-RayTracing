package me.longluo.droidutils;

public class ClickDelayUtils {

    private static final int MIN_CLICK_DELAY_TIME = 2500;

    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;

        long curClickTime = System.currentTimeMillis();

        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }

        lastClickTime = curClickTime;

        return flag;
    }

    public static long getLastClickTime() {
        return lastClickTime;
    }
}


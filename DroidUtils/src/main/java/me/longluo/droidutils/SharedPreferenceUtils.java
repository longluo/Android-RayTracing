package me.longluo.droidutils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceUtils {
    private static final String SHARED_NAME = "Launcher_pref";

    private static SharedPreferenceUtils sInstance;
    private SharedPreferences sharedReadable;
    private SharedPreferences.Editor sharedWritable;

    private SharedPreferenceUtils(Context context) {
        sharedReadable = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        sharedWritable = sharedReadable.edit();
    }

    public static SharedPreferenceUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SharedPreferenceUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferenceUtils(context);
                }
            }
        }

        return sInstance;
    }

    public String getString(String key) {
        return sharedReadable.getString(key, "");
    }

    public String getString(String key, String defaultStr) {
        return sharedReadable.getString(key, defaultStr);
    }

    public void putString(String key, String value) {
        sharedWritable.putString(key, value);
        sharedWritable.commit();
    }

    public int getInt(String key, int def) {
        return sharedReadable.getInt(key, def);
    }

    public void putInt(String key, int value) {
        sharedWritable.putInt(key, value);
        sharedWritable.commit();
    }

    public float getFloat(String key, float def) {
        return sharedReadable.getFloat(key, def);
    }

    public void putFloat(String key, float value) {
        sharedWritable.putFloat(key, value);
        sharedWritable.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedReadable.getBoolean(key, def);
    }

    public void putBoolean(String key, boolean value) {
        sharedWritable.putBoolean(key, value);
        sharedWritable.commit();
    }


}

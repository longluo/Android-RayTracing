package me.longluo.droidutils;

import android.content.Context;

public interface SystemServiceFactoryAbstract {
    Object get(Context context, String name);
}

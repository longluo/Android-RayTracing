package me.longluo.droidutils.helpers;

import android.os.Parcel;
import android.text.style.UnderlineSpan;

/**
 * WPUnderlineSpan is used as an alternative class to UnderlineSpan. UnderlineSpan is used by EditText auto
 * correct, so it can get mixed up with our formatting.
 */
public class WPUnderlineSpan extends UnderlineSpan {
    public WPUnderlineSpan() {
        super();
    }

    public WPUnderlineSpan(Parcel src) {
        super(src);
    }
}

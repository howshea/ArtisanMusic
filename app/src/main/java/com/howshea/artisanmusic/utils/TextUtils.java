package com.howshea.artisanmusic.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

/**
 * PackageName: com.howshea.artisanmusic.utils
 * FileName：   TextUtils
 * Created by haipo on 2016/12/6.
 */

public class TextUtils {

    public static SpannableString formatString(Context context, String text, int style) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(context, style), 0, text.length(), 0);
        return spannableString;
    }
}

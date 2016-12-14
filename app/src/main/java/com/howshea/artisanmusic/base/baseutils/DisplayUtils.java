package com.howshea.artisanmusic.base.baseutils;

import android.content.Context;

/**
 * PackageName: com.howshea.artisanmusic.base.baseutils
 * FileName：   DisplayUtils
 * Created by haipo on 2016/12/9.
 */

public class DisplayUtils {
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}

package com.howshea.artisanmusic.base.baseutils;

/**
 * PackageName: com.haipo.artisanmusic.base.baseutils
 * FileName：   TimeUtil
 * Created by haipo on 2016/11/16.
 */

public class TimeUtil {
    public static StringBuilder timeParse(long duration) {
        StringBuilder time = new StringBuilder("");
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time.append("0");
        }
        time.append(minute).append(":");

        if (second < 10) {
            time.append("0");
        }
        time.append(second);
        return time;
    }


}

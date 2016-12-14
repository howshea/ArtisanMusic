package com.howshea.artisanmusic.utils;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

/**
 * PackageName: com.haipo.artisanmusic.utils
 * FileName：   DBUtils
 * Created by haipo on 2016/11/9.
 */

public class DBUtils {
    public static <T> Observable<T> markObservable(final Callable<T> func){
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

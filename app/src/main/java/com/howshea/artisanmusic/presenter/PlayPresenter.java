package com.howshea.artisanmusic.presenter;

import com.howshea.artisanmusic.UI.IView.IPlay;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.model.SongLab;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.haipo.artisanmusic.presenter
 * FileName：   PlayPresenter
 * Created by haipo on 2016/11/20.
 */

public class PlayPresenter extends BasePresenter<IPlay> {

    public PlayPresenter(IPlay view) {
        super(view);
    }

    public void setIsLike(final Song song) {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int i = SongLab.get().updateSong(song);
                        subscriber.onNext(i);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer == 0) {
                            LogUtils.logi("修改歌曲失败");
                            if (isViewAttached())
                                getView().setIsLike();
                        }
                    }
                });


    }


    public void deleteSong(final long id) {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int i = SongLab.get().deleteSong(id);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}

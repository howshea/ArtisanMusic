package com.howshea.artisanmusic.presenter;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.ISongs;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.model.SongLab;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.haipo.artisanmusic.presenter
 * FileName：   SongsPresenter
 * Created by haipo on 2016/11/16.
 */

public class SongsPresenter extends BasePresenter<ISongs> {


    private ArrayList<Song> mCurrentSongs = new ArrayList<>();

    public SongsPresenter(ISongs view) {
        super(view);
    }

    public void initList(String title) {
        if (title.equals(AppApplication.getAppResources().getString(R.string.all_songs))) {
            SongLab.get().getSongsByRx()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ArrayList<Song>>() {
                        @Override
                        public void call(ArrayList<Song> songs) {
                            mCurrentSongs = new ArrayList<>(songs);
                            if (isViewAttached()) {
                                getView().closeLoading();
                                getView().setAdapter(mCurrentSongs);
                            }
                        }
                    });
        } else if (title.equals(AppApplication.getAppResources().getString(R.string.favorite_songs))) {
            getFavoriteSongs();
        } else {
            getSongsOfList(title);
        }
    }

    public void playMusic(int position, AppApplication app) {
        if (mCurrentSongs == null) {
            return;
        }
        if (isViewAttached()) {
            AppApplication.getMyEventBus().post(new PlayStatusEvent(mCurrentSongs.get(position)));
            app.playMusic(mCurrentSongs, position);
        }

    }

    public Song getItemSong(int position) {
        return mCurrentSongs.get(position);
    }


    private void getSongsOfList(final String title) {
        Observable.create(new Observable.OnSubscribe<ArrayList<Song>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Song>> subscriber) {
                ArrayList<Song> songs = SongLab.get().getSongsOfList(title);
                subscriber.onNext(songs);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Song>>() {
                    @Override
                    public void call(ArrayList<Song> songs) {
                        mCurrentSongs = songs;
                        if (songs.size() == 0) {
                            ToastUtil.showLong("快去添加歌曲吧");
                        } else {
                            if (isViewAttached()) {
                                getView().closeLoading();
                                getView().setAdapter(mCurrentSongs);
                            }
                        }
                    }
                });
    }

    private void getFavoriteSongs() {
        mCurrentSongs = new ArrayList<>();
        SongLab.get().getSongsByRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Song>>() {
                    @Override
                    public void call(ArrayList<Song> songs) {
                        if (songs != null) {
                            for (Song song : songs) {
                                if (song.isLike()) {
                                    mCurrentSongs.add(song);
                                }
                            }
                            if (isViewAttached()) {
                                getView().closeLoading();
                                getView().setAdapter(mCurrentSongs);
                            }
                        }
                    }
                });
    }


    public void deleteSong(final Song song) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int i = SongLab.get().deleteSong(song.getSongId());
                subscriber.onNext(i);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer >= 1) {
                            mCurrentSongs.remove(song);
                            if (mCurrentSongs.size() > 0 && isViewAttached()) {
                                getView().setAdapter(mCurrentSongs);
                            } else if (isViewAttached()) {
                                getView().close();
                            }
                        }
                    }
                });


    }

    public void reviseSong(final Song song) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int i = SongLab.get().updateSong(song);
                subscriber.onNext(i);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        LogUtils.logi(integer + "");
                        if (isViewAttached())
                            getView().refreshAdapter();
                    }
                });
    }

}


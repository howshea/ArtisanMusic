package com.howshea.artisanmusic.presenter;

import com.howshea.artisanmusic.UI.IView.ISongListsView;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.model.SongLab;
import com.howshea.artisanmusic.model.SongList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.haipo.artisanmusic.presenter
 * FileName：   SongListsPresenter
 * Created by haipo on 2016/11/9.
 */

public class SongListsPresenter extends BasePresenter<ISongListsView> {

    private ArrayList<Song> mlocalSongs;
    private List<SongList> mSongLists;
    private int mfavoriteSize;


    public SongListsPresenter(ISongListsView view) {
        super(view);
    }


    /**
     * 读取数据库的所有歌单
     * 如果没有歌单
     * 新建一个“所有歌曲”
     * 并且扫描媒体库
     */
    public void initLists() {
        Observable
                .create(new Observable.OnSubscribe<List<SongList>>() {
                    @Override
                    public void call(Subscriber<? super List<SongList>> subscriber) {
                        List<SongList> lists = SongLab.get().getSonglists();
                        subscriber.onNext(lists);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongList>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO
                    }

                    @Override
                    public void onNext(List<SongList> songLists) {
                        mSongLists = songLists;
                        if (isViewAttached() && songLists != null) {
                            getView().setAdapter(mSongLists);
                            getView().setListCount(mSongLists.size());
                        }

                    }
                });
    }

    public void initLocalSongs() {
        SongLab.get().getSongsByRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Song>>() {
                    @Override
                    public void call(ArrayList<Song> songs) {
                        if (songs != null) {
                            mlocalSongs = songs;
                            mfavoriteSize = 0;
                            for (Song song : songs) {
                                if (song.isLike())
                                    ++mfavoriteSize;
                            }
                            if (isViewAttached()) {
                                getView().setAllSongs(songs.size());
                                getView().setFavorite(mfavoriteSize);
                            }
                        } else {
                            if (isViewAttached()) {
                                getView().setAllSongs(0);
                                getView().setFavorite(0);
                                mlocalSongs = new ArrayList<>();
                            }
                            scanMusic();
                        }

                    }
                });
    }


    public void createSongList(String title) {
        final SongList list = new SongList();
        list.setTitle(title);
        list.setSongCount(0);
        Observable
                .create(new Observable.OnSubscribe<Long>() {
                    @Override
                    public void call(Subscriber<? super Long> subscriber) {
                        long rowId = SongLab.get().addSonglist(list);
                        subscriber.onNext(rowId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long id) {
                        if (id == -1) {
                            ToastUtil.showShort("该歌单已存在");
                        } else {
                            initLists();
                        }
                    }
                });
    }


    public void toSongList(int position) {
        if (isViewAttached()) {
            getView().toSonglist(mSongLists.get(position).getTitle());
        }
    }

    public void deleteSongList(final UUID songlistId) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean isSuccess = SongLab.get().deletSongList(songlistId);
                subscriber.onNext(isSuccess);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isSuccess) {
                        if (isSuccess) {
                            initLists();
                        }
                    }
                });
    }

    /**
     * 扫描媒体库
     *
     */
    public void scanMusic() {
        final int[] count = new int[1];

        SongLab.get().scanMusic()
                .doOnNext(new Action1<List<Song>>() {


                    @Override
                    public void call(List<Song> songs) {
                        for (Song song : songs) {
                            if (SongLab.get().getSong(song.getSongId()) == null) {
                                SongLab.get().addSong(song);
                                LogUtils.logi("增加了歌曲" + ": " + song.getTitle());
                                mlocalSongs.add(song);
                                ++count[0];
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (isViewAttached()) {
                            getView().showProgressDialog();
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        if (isViewAttached()) {
                            getView().setAllSongs(mlocalSongs.size());
                            getView().cancelProgressDialog();
                            ToastUtil.showShort("新增 " + count[0] + " 首歌");
                        }
                    }
                });
    }
}

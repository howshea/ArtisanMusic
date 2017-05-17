package com.howshea.artisanmusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.activity.PlayActivity;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.event.AppExitEvent;
import com.howshea.artisanmusic.event.IsPlayingEvent;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.event.PlayTimeEvent;
import com.howshea.artisanmusic.event.SongNoExistEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.model.SongLab;
import com.howshea.artisanmusic.utils.SystemUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PlayService extends Service {
    private static final int LIST_LOOP = 0;
    private static final int SINGLE_LOOP = 1;
    private static final int RANDOM_PLAY = 2;

    public static final String ACTION_PLAY = "playOrPause";
    public static final String ACTION_PRE = "preSong";
    public static final String ACTION_NEXT = "nextSong";
    public static final String ACTION_CLOSE = "closeAll";

    private MediaPlayer mPlayer;
    private ArrayList<Song> mSongs;
    private ArrayList<Song> mShuffleList;
    private Song mCurrentSong;
    private int mPosition;
    private Timer mTimer;
    private int mPlayMode = 0;
    private TimerTask mTask;
    private Notification mNotification;
    private RemoteViews mRemoteViews;

    private NotificationTarget notificationTarget;

    public PlayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化mediaPlayer
        mPlayer = new MediaPlayer();
//        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
        //注册EventBus事件
        AppApplication.getMyEventBus().register(this);
        //注册Broadcast ，用于接收通知栏点击事件
        PlayReceiver playReceiver = new PlayReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_PLAY);
        mFilter.addAction(ACTION_PRE);
        mFilter.addAction(ACTION_NEXT);
        mFilter.addAction(ACTION_CLOSE);
        registerReceiver(playReceiver, mFilter);
        //初始化通知栏
        Intent intent = new Intent(this, PlayActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_normal);
        builder.setContentIntent(PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContent(mRemoteViews)
                .setSmallIcon(R.drawable.ic_album_white_24dp)
                .setContentTitle("")
                .setContentText("");
        //广播事件
        Intent intentPlay = new Intent(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(getApplicationContext(), 0x123, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noti_play_pause, pendingIntentPlay);

        Intent intentPre = new Intent(ACTION_PRE);
        PendingIntent pendingIntentPre = PendingIntent.getBroadcast(getApplicationContext(), 0x124, intentPre, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noti_pre_img, pendingIntentPre);

        Intent intentNext = new Intent(ACTION_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0x125, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noti_next_img, pendingIntentNext);

        Intent intentClose = new Intent(ACTION_CLOSE);
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(getApplicationContext(), 0x126, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noti_close_img, pendingIntentClose);

        mNotification = builder.build();
        //用于Glide往通知栏插入图片的Targe
        notificationTarget = new NotificationTarget(
                getApplicationContext(),
                mRemoteViews,
                R.id.noti_album_img,
                mNotification,
                0x111);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //释放所有资源
        mPlayer.release();
        mTimer.cancel();
        mTask.cancel();
        AppApplication.getMyEventBus().unregister(this);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //发送歌曲播放进度
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (isPlaying())
                    AppApplication.getMyEventBus().post(new PlayTimeEvent(mPlayer.getCurrentPosition()));
            }
        };
        mTimer.schedule(mTask, 0, 1000);
        return new PlayBinder();
    }

    public void playMusic(ArrayList<Song> songs, int position) {
        if (songs == null) {
            return;
        }
        if (mSongs == null) {
            prepare(songs, position);
            mSongs = songs;
            mPosition = position;
            //生成一个随机列表
            createShuffleList();
        } else {
            prepare(songs, position);
            mPosition = position;
            if (!mSongs.equals(songs)) {
                LogUtils.logi("替换当前播放列表");
                mSongs = songs;
            } else {
                LogUtils.logi("播放列表未更改");
            }
            createShuffleList();
        }
    }

    private void prepare(final ArrayList<Song> songs, final int position) {
        updateNotification(songs.get(position));
        Observable
                .create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        try {
                            mCurrentSong = songs.get(position);
                            mPlayer.reset();
                            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mPlayer.setDataSource(songs.get(position).getUri());
                            mPlayer.prepare();
                            LogUtils.logi("正在播放：" + mCurrentSong.getTitle());
                        } catch (IOException e) {
                            //IO异常时捕获并发送事件通知其它组件
                            AppApplication.getMyEventBus().post(new SongNoExistEvent());
                        }
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        LogUtils.loge(e, "出错了");
                    }
                    @Override
                    public void onNext(Object o) {
                    }
                });
    }
    /**
     * 更新Notification的UI状态
     */
    private void updateNotification(Song song) {
        mRemoteViews.setTextViewText(R.id.noti_title_tv, song.getTitle());
        mRemoteViews.setTextViewText(R.id.noti_subtitle_tv, song.getArtist());
        if (song.getCoverUri() == null) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.default_cover)
                    .asBitmap()
                    .into(notificationTarget);
        } else {
            Glide.with(getApplicationContext())
                    .load(song.getCoverUri())
                    .asBitmap()
                    .placeholder(R.drawable.default_cover)
                    .into(notificationTarget);
        }
        mRemoteViews.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_noti_pause);
        startForeground(0x111, mNotification);
    }

    //用于bottomdialog的点击换歌
    public void playInList(int position) {
        playOne(mSongs, position);
        mPosition = position;
    }
    //替换播放列表
    private void playOne(ArrayList<Song> songs, int position) {
        AppApplication.getMyEventBus().post(new PlayStatusEvent(songs.get(position)));
        prepare(songs, position);
    }

    public void nextSong() {
        if (mSongs == null) {
            return;
        }
        switch (mPlayMode) {
            case LIST_LOOP:
                if (mPosition + 1 == mSongs.size()) {
                    mPosition = 0;
                } else {
                    ++mPosition;
                }
                playOne(mSongs, mPosition);
                break;
            case SINGLE_LOOP:
                playOne(mSongs, mPosition);
                break;
            case RANDOM_PLAY:
                if (mPosition + 1 == mSongs.size()) {
                    mPosition = 0;
                } else {
                    ++mPosition;
                }
                playOne(mShuffleList, mPosition);
                break;
        }

    }

    public void preSong() {
        if (mSongs == null) {
            return;
        }

        switch (mPlayMode) {
            case LIST_LOOP:
                if (mPosition == 0) {
                    mPosition = mSongs.size() - 1;
                } else {
                    --mPosition;
                }
                playOne(mSongs, mPosition);
                break;
            case SINGLE_LOOP:
                playOne(mSongs, mPosition);
                break;
            case RANDOM_PLAY:
                if (mPosition == 0) {
                    mPosition = mSongs.size() - 1;
                } else {
                    --mPosition;
                }
                playOne(mShuffleList, mPosition);
                break;
        }
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void setPlayMode(int mode) {
        mPlayMode = mode;
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    @SuppressWarnings("unused")
    public void rePlay() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    @SuppressWarnings("unused")
    public void pauseMusic() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
    }

    public void playOrPause() {
        if (mSongs == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mRemoteViews.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_noti_play);
            startForeground(0x111, mNotification);
            AppApplication.getMyEventBus().post(new IsPlayingEvent(isPlaying()));
        } else {
            mPlayer.start();
            mRemoteViews.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_noti_pause);
            startForeground(0x111, mNotification);
            AppApplication.getMyEventBus().post(new IsPlayingEvent(isPlaying()));
        }
    }

    public void stopMusic() {
        //判断app是否在前台,如果不在前台则杀死app进程
        if (!SystemUtil.isForeground(getApplicationContext())) {
            stopForeground(true);
            AppApplication.getMyEventBus().post(new AppExitEvent());
            System.exit(0);
        } else {
            if (isPlaying())
                mPlayer.pause();
            mPlayer.seekTo(0);
            AppApplication.getMyEventBus().post(new PlayTimeEvent(mPlayer.getCurrentPosition()));
            mRemoteViews.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_noti_play);
            AppApplication.getMyEventBus().post(new IsPlayingEvent(isPlaying()));
            stopForeground(true);
        }
    }

    public PlayStatusEvent getCurrentSongInfo() {
        if (mSongs == null)
            return null;
        PlayStatusEvent event = new PlayStatusEvent(mCurrentSong);
        event.setPlaying(isPlaying());
        return event;
    }

    public boolean isDataPrepared() {
        return mSongs != null;
    }

    public void seekTo(int position) {
        mPlayer.seekTo(position);
    }

    public ArrayList<Song> getCurrentPlayList() {
        return mSongs;
    }

    /**
     * 随机播放算法
     * @param list 新的list
     */
    private static void shuffle(ArrayList<Song> list) {
        int key;
        Song temp;
        Random rand = new Random();
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                key = rand.nextInt(list.size() - 1);
                temp = list.get(i);
                list.set(i, list.get(key));
                list.set(key, temp);
            }
        }

    }

    /**
     * 创建随机播放列表
     */
    private void createShuffleList() {
        mShuffleList = new ArrayList<>(mSongs);
        shuffle(mShuffleList);
        //当播放模式处于随机播放时，校准位置
        if (mPlayMode == RANDOM_PLAY) {
            for (int i = 0; i < mShuffleList.size(); i++) {
                if (mShuffleList.get(i).equals(mSongs.get(mPosition))) {
                    mPosition = i;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void removeMistakeSong(SongNoExistEvent event) {
        int i = SongLab.get().deleteSong(mCurrentSong.getSongId());
        LogUtils.logi("删除" + i + "行");
    }

    public void deleteSong(Song song) {
        if (mSongs.contains(song)) {
            mShuffleList.remove(song);
            mSongs.remove(song);
        }
        if (mCurrentSong.equals(song)) {
            playOne(mSongs, mPosition);
        }
    }

    public class PlayBinder extends Binder {
        /**
         * @return a reference of the outer service
         */
        public PlayService getService() {
            return PlayService.this;
        }
    }

    /**
     * 定义一个BroadcastReceiver，用于前台和Service的交互动作
     */
    public class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();// 获取对应Action
            LogUtils.logi(action);
            switch (action) {
                case PlayService.ACTION_PLAY:
                    PlayService.this.playOrPause();
                    break;
                case PlayService.ACTION_PRE:
                    PlayService.this.preSong();
                    break;
                case PlayService.ACTION_NEXT:
                    PlayService.this.nextSong();
                    break;
                case PlayService.ACTION_CLOSE:
                    PlayService.this.stopMusic();
                    break;
            }
        }
    }
}

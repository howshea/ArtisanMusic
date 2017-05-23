package com.howshea.artisanmusic.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.howshea.artisanmusic.BuildConfig;
import com.howshea.artisanmusic.base.baseapp.BaseApplication;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.service.PlayService;
import com.howshea.artisanmusic.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by haipo
 * on 2016/11/1.
 */

public class AppApplication extends BaseApplication {


    private static EventBus mEventBus;
    private PlayService mPlayService;
    private Intent mServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.logInit(BuildConfig.LOG_DEBUG);
        mEventBus = EventBus.builder().addIndex(new MyEventBusIndex()).build();
        startAndBindService();
    }


    public void stopService(){
        unbindService(conn);
        stopService(mServiceIntent);
    }


    public static EventBus getMyEventBus() {
        return mEventBus;
    }

    private void startAndBindService() {
        mServiceIntent = new Intent(this, PlayService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
    }

    public void setPlayMode(int mode) {
        mPlayService.setPlayMode(mode);
    }

    public int getPlayMode() {
        return mPlayService.getPlayMode();
    }

    public void playMusic(ArrayList<Song> songs, int position) {
        mPlayService.playMusic(songs, position);
    }

    public void nextSong() {
        mPlayService.nextSong();
    }

    public void preSong() {
        mPlayService.preSong();
    }

    public void playOrPause() {
        mPlayService.playOrPause();
    }

    /**
     * 获取当前播放信息
     */
    public PlayStatusEvent getCurrentSongInfo() {
        if (mPlayService != null) {
            return mPlayService.getCurrentSongInfo();
        }
        return null;
    }

    /**
     *
     * @return mediaplayer是否有资源
     */
    public boolean isDataPrepared() {
        return mPlayService != null && mPlayService.isDataPrepared();
    }

    public void seekTo(int position) {
        mPlayService.seekTo(position);
    }

    public ArrayList<Song> getCurrentPlayList() {
        return mPlayService.getCurrentPlayList();
    }

    public void playInList(int position) {
        mPlayService.playInList(position);
    }

    public void deleteSong(Song song) {
        if (isDataPrepared())
            mPlayService.deleteSong(song);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}

package com.howshea.artisanmusic.base.baseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.activity.PlayActivity;
import com.howshea.artisanmusic.UI.dialog.PlayListBottomFragment;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.basemvp.IBaseView;
import com.howshea.artisanmusic.event.AppExitEvent;
import com.howshea.artisanmusic.event.IsPlayingEvent;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.utils.ImageUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * PackageName: com.howshea.artisanmusic.base.baseapp
 * FileName：   BasePlayBarMVPActivity
 * Created by haipo on 2016/12/6.
 */

public abstract class BasePlayBarMVPActivity<V extends IBaseView, P extends BasePresenter<V>> extends AppCompatActivity {
    @BindView(R.id.playbar_cover_image_view)
    ImageView mSongImageView;
    @BindView(R.id.playbar_title_text_view)
    TextView mSongTitleTextView;
    @BindView(R.id.playbar_dec_text_view)
    TextView mSongDecTextView;
    @BindView(R.id.playbar_play_pause_image_view)
    ImageView mPlayImageView;
    @BindView(R.id.play_bottom_bar)
    LinearLayout mPlayBottomBar;

    protected boolean isPlaying = true;
    protected P mPresenter;

    /**
     * @return 布局文件
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * @return presenter对象
     */
    protected abstract P getPresenter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mPresenter = getPresenter();
        AppApplication.getMyEventBus().register(this);
        if (((AppApplication) getApplication()).isDataPrepared()) {
            setBottomBar(((AppApplication) getApplication()).getCurrentSongInfo());
        } else {
            mPlayBottomBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPlayBottomBar.getVisibility() == View.VISIBLE) {
            setBottomBar(((AppApplication) getApplication()).getCurrentSongInfo());
        }
    }


    @Override
    protected void onDestroy() {
        AppApplication.getMyEventBus().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setBarEvent(PlayStatusEvent event) {
        setBottomBar(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPlayOrPause(IsPlayingEvent event) {
        isPlaying = event.isPlaying();
        mPlayImageView.setSelected(isPlaying);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(AppExitEvent event){
        finish();
    }


    protected void setBottomBar(PlayStatusEvent event) {
        if (mPlayBottomBar.getVisibility() == View.GONE) {
            mPlayBottomBar.setVisibility(View.VISIBLE);
        }
        Song song = event.getSong();
        mSongTitleTextView.setText(song.getTitle());
        mSongDecTextView.setText(song.getArtist());
        ImageUtils.setSongImage(song.getCoverUri(), mSongImageView);

        isPlaying = event.isPlaying();
        mPlayImageView.setSelected(isPlaying);
    }

    protected int getPlaybarStatus() {
        return mPlayBottomBar.getVisibility();
    }

    @OnClick(R.id.play_bottom_bar)
    void toPlayPage(View v) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.playbar_next_song_image_view)
    void nextSong(View view) {
        ((AppApplication) getApplication()).nextSong();
    }

    @OnClick(R.id.playbar_playlist_image_view)
    void expandPlayList(View v) {
        FragmentManager manager = getSupportFragmentManager();
        PlayListBottomFragment dialog = PlayListBottomFragment.newInstance();
        dialog.show(manager, PlayActivity.PLAYLISTDIALOG);
    }

    @OnClick(R.id.playbar_play_pause_image_view)
    void palyOrPause(ImageView view) {
//        ((AppApplication) getApplication())
        if (isPlaying) {
            isPlaying = false;
            mPlayImageView.setSelected(false);
        } else {
            isPlaying = true;
            mPlayImageView.setSelected(true);
        }
        ((AppApplication) getApplication()).playOrPause();
    }


}

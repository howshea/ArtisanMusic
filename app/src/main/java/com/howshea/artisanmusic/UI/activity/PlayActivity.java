package com.howshea.artisanmusic.UI.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.IPlay;
import com.howshea.artisanmusic.UI.dialog.PlayListBottomFragment;
import com.howshea.artisanmusic.UI.dialog.SongEditDialog;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.basemvp.BaseActivtiy;
import com.howshea.artisanmusic.base.baseutils.TimeUtil;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.event.AppExitEvent;
import com.howshea.artisanmusic.event.IsPlayingEvent;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.event.PlayTimeEvent;
import com.howshea.artisanmusic.event.SongNoExistEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.presenter.PlayPresenter;
import com.howshea.artisanmusic.utils.ImageUtils;
import com.howshea.widgets.VinylRecord;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayActivity extends BaseActivtiy<IPlay, PlayPresenter> implements IPlay {

    @BindView(R.id.background_image_view)
    ImageView mBackgroundImageView;
    @BindView(R.id.play_tool_bar)
    Toolbar mPlayToolBar;
    @BindView(R.id.play_time_text_view)
    TextView mPlayTimeTextView;
    @BindView(R.id.play_seek_bar)
    SeekBar mPlaySeekBar;
    @BindView(R.id.play_duration_text_view)
    TextView mPlayDurationTextView;
    @BindView(R.id.play_mode_image_view)
    ImageView mPlayModeImageView;
    @BindView(R.id.prev_song_image_view)
    ImageView mPrevSongImageView;
    @BindView(R.id.play_pause_image_view)
    ImageView mPlayPauseImageView;
    @BindView(R.id.next_song_image_view)
    ImageView mNextSongImageView;
    @BindView(R.id.play_list_image_view)
    ImageView mPlayListImageView;
    @BindView(R.id.play_container)
    FrameLayout mPlayContainer;
    @BindView(R.id.vinyl_record_surface_view)
    VinylRecord mVinylRecordView;
    @BindView(R.id.play_like_img)
    ImageView mPlayLikeImg;

    private AppApplication mApp;
    private boolean isPlaying;
    private int mPlayMode = 0;
    public static final String PLAYLISTDIALOG = "playlistdialog";
    public static final String SONGEDITDIALOG = "songeditdialog";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected PlayPresenter getPresenter() {
        return new PlayPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        AppApplication.getMyEventBus().register(this);
        mApp = (AppApplication) getApplication();
        setInterface(mApp.getCurrentSongInfo());
        initSeekBarListener();

    }

    @Override
    protected void onRestart() {
        setInterface(mApp.getCurrentSongInfo());
        super.onRestart();
    }

//    @Override
//    protected void onResume() {
//        setInterface(mApp.getCurrentSongInfo());
//        super.onResume();
//    }

    @Override
    protected void onDestroy() {
        AppApplication.getMyEventBus().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setInterfaceEvent(PlayStatusEvent event) {
        //先置为零,避免切换歌曲时，由于seekbar的max变化，造成的seekbar指示器显示异常
        mPlaySeekBar.setProgress(0);
        mPlayTimeTextView.setText("00:00");
        setInterface(event);
        mVinylRecordView.switchSong();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSeekBarPosition(PlayTimeEvent event) {
        mPlaySeekBar.setProgress(event.getTime());
        mPlayTimeTextView.setText(TimeUtil.timeParse(event.getTime()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNoExistException(SongNoExistEvent event) {
        ToastUtil.showLong("歌曲不存在，已移除");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPlayOrPause(IsPlayingEvent event) {
        isPlaying = event.isPlaying();
        mPlayPauseImageView.setSelected(isPlaying);
        mVinylRecordView.setIsPlaying(isPlaying);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(AppExitEvent event){
        finish();
    }

    private void initStatusBar() {
        //设置状态栏透明
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setSupportActionBar(mPlayToolBar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setInterface(PlayStatusEvent event) {
        if (event == null) {
            return;
        }
        Song song = event.getSong();
        ImageUtils.setSongBackground(song.getCoverUri(), mBackgroundImageView);
//        mTitleTextView.setText(event.getSong().getTitle());
//        mSubtitleTextView.setText(event.getSong().getArtist());
        getSupportActionBar().setTitle(song.getTitle());
        getSupportActionBar().setSubtitle(song.getArtist());
        mPlayDurationTextView.setText(TimeUtil.timeParse(song.getDuration()));
        isPlaying = event.isPlaying();
        mPlayPauseImageView.setSelected(isPlaying);
        mPlayModeImageView.setImageLevel(mApp.getPlayMode());
        mPlayMode = mApp.getPlayMode();
        mPlaySeekBar.setMax((int) song.getDuration());

        mVinylRecordView.setCoverBitmap(song.getCoverUri());
        mVinylRecordView.setIsPlaying(event.isPlaying());
        mPlayLikeImg.setSelected(song.isLike());
    }

    public void initSeekBarListener() {
        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mApp.seekTo(progress);
            }
        });
    }

    @Override
    public void setIsLike() {
        if (mPlayLikeImg.isSelected())
            mPlayLikeImg.setSelected(false);
        else {
            mPlayLikeImg.setSelected(true);
        }
    }

    @OnClick(R.id.play_menu_img)
    void showSongEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SongEditDialog dialog = SongEditDialog.newInstance(getSupportActionBar().getTitle().toString());
        dialog.show(fm, SONGEDITDIALOG);
    }

    @OnClick(R.id.play_like_img)
    void switchHeart(ImageView view) {
        setIsLike();
        Song song = mApp.getCurrentSongInfo().getSong();
        song.setLike(mPlayLikeImg.isSelected());
        mPresenter.setIsLike(song);
    }

    @OnClick(R.id.play_delete_img)
    void deleteSong() {
        mPresenter.deleteSong(mApp.getCurrentSongInfo().getSong().getSongId());
        mApp.deleteSong(mApp.getCurrentSongInfo().getSong());
    }

    @OnClick(R.id.play_mode_image_view)
    void switchPlayMode(ImageView v) {
        ++mPlayMode;
        if (mPlayMode > 2) {
            mPlayMode = 0;
        }
        v.setImageLevel(mPlayMode);
        mApp.setPlayMode(mPlayMode);
        switch (mPlayMode) {
            case 0:
                ToastUtil.showShort(R.string.list_loop);
                break;
            case 1:
                ToastUtil.showShort(R.string.single_loop);
                break;
            case 2:
                ToastUtil.showShort(R.string.random_play);
                break;
        }
    }


    @OnClick(R.id.prev_song_image_view)
    void preSong(ImageView v) {
        mApp.preSong();
    }


    @OnClick(R.id.play_pause_image_view)
    void playOrPause(ImageView v) {
        if (isPlaying) {
            isPlaying = false;
            mPlayPauseImageView.setSelected(false);
            mVinylRecordView.setIsPlaying(false);
        } else {
            isPlaying = true;
            mPlayPauseImageView.setSelected(true);
            mVinylRecordView.setIsPlaying(true);

        }
        mApp.playOrPause();
    }

    @OnClick(R.id.next_song_image_view)
    void nextSong(ImageView v) {
        mApp.nextSong();
    }

    @OnClick(R.id.play_list_image_view)
    void expendPlayList(ImageView v) {
        FragmentManager manager = getSupportFragmentManager();
        PlayListBottomFragment dialog = PlayListBottomFragment.newInstance();
        dialog.show(manager, PLAYLISTDIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

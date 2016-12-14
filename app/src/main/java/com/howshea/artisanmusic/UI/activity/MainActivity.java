package com.howshea.artisanmusic.UI.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.dialog.PlayListBottomFragment;
import com.howshea.artisanmusic.UI.fragment.SongListsFragment;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.event.AppExitEvent;
import com.howshea.artisanmusic.event.IsPlayingEvent;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.event.SongNoExistEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.utils.ImageUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_view_pager)
    ViewPager mMainViewPager;
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

    private boolean isPlaying = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        AppApplication.getMyEventBus().register(this);
        mMainViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.main_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        AppApplication.getMyEventBus().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setBarEvent(PlayStatusEvent event) {
        setBottomBar(event);
    }

    private void setBottomBar(PlayStatusEvent event) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPlayOrPause(IsPlayingEvent event) {
        isPlaying = event.isPlaying();
        mPlayImageView.setSelected(isPlaying);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(AppExitEvent event) {
        finish();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SongListsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNoExistException(SongNoExistEvent event) {
        ToastUtil.showLong("歌曲不存在，已移除");
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

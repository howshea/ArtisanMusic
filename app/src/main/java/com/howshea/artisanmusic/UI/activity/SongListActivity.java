package com.howshea.artisanmusic.UI.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.ISongs;
import com.howshea.artisanmusic.UI.dialog.EditSongBottomDialog;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.baseapp.BasePlayBarMVPActivity;
import com.howshea.artisanmusic.base.baseutils.TimeUtil;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.event.DeleteSongEvent;
import com.howshea.artisanmusic.event.PlayStatusEvent;
import com.howshea.artisanmusic.event.SongNoExistEvent;
import com.howshea.artisanmusic.event.UpdateSongEvent;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.presenter.SongsPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

public class SongListActivity extends BasePlayBarMVPActivity<ISongs, SongsPresenter> implements ISongs {
    private static final String EXTRA_SONGS = "com.haipo.artisanmusic.songs";

    @BindView(R.id.song_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout)
    LinearLayout mloadingLayout;
    private String mSonglistTitle;


    public static Intent newIntent(Context packageContext, String songListTitle) {
        Intent intent = new Intent(packageContext, SongListActivity.class);
        intent.putExtra(EXTRA_SONGS, songListTitle);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_songs;
    }

    @Override
    protected SongsPresenter getPresenter() {
        return new SongsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSonglistTitle = getIntent().getStringExtra(EXTRA_SONGS);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(mSonglistTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.initList(mSonglistTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter(ArrayList<Song> songs) {
        mRecyclerView.setAdapter(new SongsListAdapter(songs));
        if (getPlaybarStatus() == View.VISIBLE) {
            ((SongsListAdapter) mRecyclerView.getAdapter()).addFootView();
        }
    }

    @Override
    protected void setBottomBar(PlayStatusEvent event) {
        super.setBottomBar(event);
        if (getPlaybarStatus() == View.VISIBLE && mRecyclerView.getAdapter() != null) {
            ((SongsListAdapter) mRecyclerView.getAdapter()).addFootView();
        }
    }

    @Override
    public void closeLoading() {
        mloadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void refreshAdapter() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNoExistException(SongNoExistEvent event) {
        ToastUtil.showLong("歌曲不存在，已移除");
        refreshAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteSong(DeleteSongEvent event) {
        mPresenter.deleteSong(event.getDeleteSong());
        ((AppApplication) getApplication()).deleteSong(event.getDeleteSong());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reviseSong(UpdateSongEvent event) {
        mPresenter.reviseSong(event.getRevisedSong());
    }


    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTitleTextView;
        private final TextView mDescTextView;
        private StringBuilder mDesc;
        private final View mDividerView;
        private final ImageView mMenuImageView;

        public Holder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.song_title_text_view);
            mDescTextView = (TextView) itemView.findViewById(R.id.song_desc_text_view);
            mMenuImageView = (ImageView) itemView.findViewById(R.id.song_menu_image_view);
            mDividerView = itemView.findViewById(R.id.divider_view);
            mDesc = new StringBuilder("");
            itemView.setOnClickListener(this);
            mMenuImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    EditSongBottomDialog dialog = EditSongBottomDialog.newInstance(mPresenter.getItemSong(getAdapterPosition()));
                    dialog.show(fm, "EditSongBottomDialog");
                }
            });
        }

        public void BindData(Song song) {
            mTitleTextView.setText(song.getTitle());
            mDescTextView.setText(mDesc.append(song.getArtist())
                    .append(" - ")
                    .append(song.getAlbum())
                    .append(" | ")
                    .append(TimeUtil.timeParse(song.getDuration())));
//            mDividerView.setVisibility(View.GONE);
            mDesc.delete(0, mDesc.length());
        }

        @Override
        public void onClick(View v) {
            if (!mTitleTextView.getText().equals(""))
                mPresenter.playMusic(getAdapterPosition(), (AppApplication) getApplication());
        }
    }


    private class SongsListAdapter extends RecyclerView.Adapter<Holder> {

        private ArrayList<Song> mSongs;
        private int mItemCount;

        public SongsListAdapter(ArrayList<Song> songs) {
            mSongs = songs;
            mItemCount = mSongs.size();
        }

        /**
         * 为了实现播放条透明且不会遮住最后一个item
         */
        public void addFootView() {
            if (mItemCount == mSongs.size()) {
                mItemCount = mSongs.size() + 1;
                notifyDataSetChanged();
            }
        }

        public void cancelFootView() {
            if (mItemCount == mSongs.size() + 1) {
                mItemCount = mSongs.size();
                notifyDataSetChanged();
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SongListActivity.this).inflate(R.layout.item_song_info, parent, false);

            //下面三行是添加RecyclerView的点击效果
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            view.setBackgroundResource(typedValue.resourceId);


            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            if (position == mSongs.size()) {
                holder.mTitleTextView.setText("");
                holder.mDescTextView.setText("");
                holder.mDividerView.setVisibility(View.GONE);
                holder.mMenuImageView.setVisibility(View.GONE);
            } else {
                holder.mDividerView.setVisibility(View.VISIBLE);
                holder.mMenuImageView.setVisibility(View.VISIBLE);
                holder.BindData(mSongs.get(position));
            }
        }

        @Override
        public int getItemCount() {
            //最后加一项空白

            return mItemCount;
        }
    }
}

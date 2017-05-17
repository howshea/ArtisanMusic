package com.howshea.artisanmusic.UI.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.ISongListsView;
import com.howshea.artisanmusic.UI.activity.SongListActivity;
import com.howshea.artisanmusic.UI.adapter.SongListViewAdapter;
import com.howshea.artisanmusic.UI.dialog.EditSongListDialog;
import com.howshea.artisanmusic.UI.dialog.NewSongListDialogFragment;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.basemvp.BaseFragment;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.event.CreateListEvent;
import com.howshea.artisanmusic.model.SongList;
import com.howshea.artisanmusic.presenter.SongListsPresenter;
import com.howshea.artisanmusic.utils.TextUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * PackageName: com.haipo.artisanmusic.UI.fragment
 * FileName：   SongListsFragment
 * Created by haipo on 2016/11/7.
 */

public class SongListsFragment extends BaseFragment<ISongListsView, SongListsPresenter> implements ISongListsView, SongListViewAdapter.Callback {

    @BindView(R.id.lists_list_view)
    ListView mListsListView;
    @BindView(R.id.all_songs_text_view)
    TextView mAllSongsTextView;
    @BindView(R.id.favorite_songs_text_view)
    TextView mFavoriteSongsTextView;
    @BindView(R.id.lists_count_tv)
    TextView mListsCount;

    private static final String TAG_NEWLIST = "NewSongListDialog";
    private static final String TAG_EDITLIST = "EditSongListDialog";
    private ProgressDialog mDialog;

    public static SongListsFragment newInstance() {
        return new SongListsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_main_play_lists;
    }

    @Override
    protected SongListsPresenter getPresenter() {
        return new SongListsPresenter(this);
    }

    @Override
    protected void initView() {
        mListsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.toSongList(i);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppApplication.getMyEventBus().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.initLocalSongs();
        mPresenter.initLists();
    }

    @Override
    public void onDestroyView() {
        AppApplication.getMyEventBus().unregister(this);
        super.onDestroyView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exitApp:
                ((AppApplication) getActivity().getApplication()).stopService();
                getActivity().finish();
                System.exit(0);
                break;
            case R.id.scanSD:
                mPresenter.scanMusic();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListCount(int size) {
        mListsCount.setText("创建的歌单(" + size + ")");
    }

    @Override
    public void showProgressDialog() {
        mDialog = ProgressDialog.show(getActivity(),
                "正在扫描媒体库", "请等待");
    }

    @Override
    public void cancelProgressDialog() {
        mDialog.dismiss();
    }

    @Override
    public void setAllSongs(int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.all_songs)).append(TextUtils.formatString(getActivity(), " （" + count + "）", R.style.SmallTextAppearance));
        mAllSongsTextView.setText(builder);
    }

    @Override
    public void setFavorite(int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.favorite_songs)).append(TextUtils.formatString(getActivity(), " (" + count + ")", R.style.SmallTextAppearance));
        mFavoriteSongsTextView.setText(builder);
    }

    @Override
    public void setAdapter(List<SongList> songLists) {
        if (songLists.size() != 0)
            mListsListView.setAdapter(new SongListViewAdapter(songLists, this));
    }

    @Override
    public void toSonglist(String songListTitle) {
        Intent i = SongListActivity.newIntent(getActivity(), songListTitle);
        startActivity(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createSongList(CreateListEvent event) {
        LogUtils.logi(event.getListTitle());
        mPresenter.createSongList(event.getListTitle());
    }


    @OnClick(R.id.all_songs_layout)
    void toAllSongs() {
        toSonglist(getString(R.string.all_songs));
    }

    @OnClick(R.id.favorite_songs_layout)
    void toFavorite() {
        toSonglist(getString(R.string.favorite_songs));
    }

    @OnClick(R.id.add_play_list_btn)
    void addPlayList() {
        FragmentManager manager = getFragmentManager();
        NewSongListDialogFragment dialog = NewSongListDialogFragment.newInstance();
        dialog.show(manager, TAG_NEWLIST);

    }

    @Override
    public void expandPopupMenu(final SongList songList, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.songlist_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_menu_delete:
                        mPresenter.deleteSongList(songList.getId());
                        break;
                    case R.id.list_menu_edit:
                        EditSongListDialog dialog = EditSongListDialog.newInstance(songList.getId(), songList.getTitle());
                        FragmentManager fm = getFragmentManager();
                        dialog.show(fm, TAG_EDITLIST);
                        getFragmentManager().executePendingTransactions();
                        dialog.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mPresenter.initLists();
                            }
                        });
                        break;

                }
                return true;
            }
        });
        popupMenu.show();
    }


}

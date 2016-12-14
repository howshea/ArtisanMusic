package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.model.Song;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   SongEditDialog
 * Created by haipo on 2016/12/9.
 */

public class SongEditDialog extends DialogFragment {


    @BindView(R.id.song_edit_dialog_title)
    TextView mSongEditDialogTitle;
    @BindView(R.id.song_edit_singer_name_tv)
    TextView mSingerName;
    @BindView(R.id.song_edit_album_name_tv)
    TextView mAlbumName;

    private String mTitle;
    private Unbinder mUnbinder;
    private static final String ARG_TITLE = "title";
    private static final String TAG = "songlistsdialog";
    private AppApplication mApp;
    private Song mSong;


    public static SongEditDialog newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        SongEditDialog fragment = new SongEditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getString(ARG_TITLE);
        mApp = (AppApplication) getActivity().getApplication();
        mSong = mApp.getCurrentSongInfo().getSong();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_dialog_play_song_edit, null);
        mUnbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);

        mSongEditDialogTitle.setText(new StringBuilder("歌曲：").append(mTitle));

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        mSingerName.setText(new StringBuilder("歌手：").append(mSong.getArtist()));
        mAlbumName.setText(new StringBuilder("专辑：").append(mSong.getAlbum()));
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.add_to_list_tv)
    void addtoList() {
        dismiss();
        FragmentManager fm = getFragmentManager();
        SongListsDialog dialog = SongListsDialog.newInstance(mSong.getSongId());
        dialog.show(fm, TAG);
    }


}

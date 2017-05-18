package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.event.DeleteSongEvent;
import com.howshea.artisanmusic.model.Song;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   EditSongBottomDialog
 * Created by haipo on 2016/12/11.
 */

public class EditSongBottomDialog extends BottomSheetDialogFragment {

    private static final String ARG_SONG = "song";
    private static final String TAG_REVISEDIALOG = "reviseSongInfo";
    @BindView(R.id.edit_song_dialog_title)
    TextView mDialogTitle;
    @BindView(R.id.edit_song_delete_tv)
    TextView mDeleteTv;
    @BindView(R.id.edit_song_singer_name_tv)
    TextView mSingerNameTv;
    @BindView(R.id.edit_song_album_name_tv)
    TextView mAlbumNameTv;
    private Song mSong;
    private Unbinder mUnbinder;

    public static EditSongBottomDialog newInstance(Song song) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG, song);
        EditSongBottomDialog fragment = new EditSongBottomDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSong = (Song) getArguments().getSerializable(ARG_SONG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_dialog_edit_song, null);
        mUnbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDialogTitle.setText(new StringBuilder("歌曲：").append(mSong.getTitle()));
        mSingerNameTv.setText(new StringBuilder("歌手：").append(mSong.getArtist()));
        mAlbumNameTv.setText(new StringBuilder("专辑：").append(mSong.getAlbum()));
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.edit_song_delete_tv)
    void deleteSong() {
        AppApplication.getMyEventBus().post(new DeleteSongEvent(mSong));
        dismiss();
    }

    @OnClick(R.id.edit_song_edit_tv)
    void editSongInfo() {
        FragmentManager fm = getFragmentManager();
        ReviseSongInfoDialog dialog = ReviseSongInfoDialog.newInstance(mSong);
        dialog.show(fm, TAG_REVISEDIALOG);
        dismiss();
    }

}

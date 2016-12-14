package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.event.UpdateSongEvent;
import com.howshea.artisanmusic.model.Song;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   ReviseSongInfoDialog
 * Created by haipo on 2016/12/12.
 */

public class ReviseSongInfoDialog extends DialogFragment {
    @BindView(R.id.revise_song_title_et)
    EditText mTitleEt;
    @BindView(R.id.revise_singer_name_et)
    EditText mSingerNameEt;
    @BindView(R.id.revise_album_et)
    EditText mAlbumEt;


    private static final String ARG_SONG = "song";
    private Unbinder mUnbinder;
    private String mTitle;
    private String mSingerName;
    private String mAlbumName;
    private Song mSong;

    public static ReviseSongInfoDialog newInstance(Song song) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG, song);
        ReviseSongInfoDialog fragment = new ReviseSongInfoDialog();
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
        View view = View.inflate(getActivity(), R.layout.dialog_revise_songinfo, null);
        mUnbinder = ButterKnife.bind(this, view);
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setView(view)
                .setTitle("修改歌曲信息")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTitle = mTitleEt.getText().toString().trim();
                        if (mTitle.length() == 0)
                            mTitle = mSong.getFileName();
                        mSong.setTitle(mTitle);
                        mSingerName = mSingerNameEt.getText().toString().trim();
                        if (mSingerName.length() == 0) {
                            mSingerName = "未知";
                        }
                        mSong.setArtist(mSingerName);
                        mAlbumName = mAlbumEt.getText().toString().trim();
                        if (mAlbumName.length() == 0) {
                            mAlbumName = "未知";
                        }
                        mSong.setAlbum(mAlbumName);
                        AppApplication.getMyEventBus().post(new UpdateSongEvent(mSong));
                        dialog.cancel();
                    }
                })
                .create();
        return dialog;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        mTitleEt.setText(mSong.getTitle());
        mSingerNameEt.setText(mSong.getArtist());
        mAlbumEt.setText(mSong.getAlbum());
    }
}

package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.model.SongLab;
import com.howshea.artisanmusic.model.SongList;

import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   EditSongListDialog
 * Created by haipo on 2016/12/11.
 */

public class EditSongListDialog extends DialogFragment {
    private EditText mTitleEditText;
    private TextView mTitleCount;
    private AlertDialog mDialog;
    private String mTitle;
    private static final String ARG_UUID = "UUID";
    private static final String ARG_TITLE = "TITLE";
    private UUID mId;

    public static EditSongListDialog newInstance(UUID uuid, String title) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_UUID, uuid);
        args.putString(ARG_TITLE, title);
        EditSongListDialog fragment = new EditSongListDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = (UUID) getArguments().getSerializable(ARG_UUID);
        mTitle = getArguments().getString(ARG_TITLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.dialog_new_list, null);
        mTitleEditText = (EditText) view.findViewById(R.id.song_list_title_edit_text);
        mTitleCount = (TextView) view.findViewById(R.id.title_count_text_view);
        mTitleEditText.setText(mTitle);
        mTitleCount.setText(mTitle.length() + "/30");
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitleCount.setText(new StringBuilder().append(s.length()).append("/30"));
                if (s.length() > 30 || s.length() == 0)
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                else mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                mTitle = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mDialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setView(view)
                .setTitle("编辑标题")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.cancel();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit(mId, mTitle);
                    }
                })
                .create();
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void edit(final UUID id, final String title) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                SongList songList = SongLab.get().getSongList(id);
                songList.setTitle(title);
                int i = SongLab.get().updateSongList(songList);
                subscriber.onNext(i);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer == 0) {
                            ToastUtil.showShort("未修改");
                        } else {
                            ToastUtil.showShort("编辑完成");
                            mDialog.cancel();
                        }
                    }
                });
    }
}

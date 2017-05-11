package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.event.CreateListEvent;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   NewSongListDialogFragment
 * Created by haipo on 2016/12/9.
 */

public class NewSongListDialogFragment extends DialogFragment {

    private AlertDialog mDialog;
    private TextView mTitleCount;
    private String mTitle;

    public static NewSongListDialogFragment newInstance() {
        return new NewSongListDialogFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.dialog_new_list, null);
        EditText mTitleEditText = (EditText) view.findViewById(R.id.song_list_title_edit_text);
        mTitleCount = (TextView) view.findViewById(R.id.title_count_text_view);
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
                .setTitle("新建歌单")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.cancel();
                    }
                })
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppApplication.
                                getMyEventBus().
                                post(new CreateListEvent(mTitle));
                        mDialog.cancel();
                    }
                })
                .create();
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set width of the dialog
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //以百分比设置大小
        mDialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }
}

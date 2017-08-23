package com.howshea.artisanmusic.UI.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.utils.TextUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * PackageName: com.howshea.artisanmusic.UI.fragment
 * FileName：   PlayListBottomFragment
 * Created by haipo on 2016/12/6.
 */

public class PlayListBottomFragment extends BottomSheetDialogFragment {

    @BindView(R.id.bottom_title_text_view)
    TextView mBottomTitleTextView;
    @BindView(R.id.clear_text_view)
    TextView mClearTextView;
    @BindView(R.id.bottom_play_recycler_view)
    RecyclerView mBottomPlayRecyclerView;
    private AppApplication mApp;
    private ArrayList<Song> mPlayList;
    private Song mCurrentSong;
    private Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (AppApplication) getActivity().getApplication();
        mPlayList = mApp.getCurrentPlayList();
        mCurrentSong = mApp.getCurrentSongInfo().getSong();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomTitleTextView.setText(new StringBuilder("播放列表").append("（").append(mPlayList.size()).append("）"));
        mBottomPlayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBottomPlayRecyclerView.setAdapter(new PlayListAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mPlayList.size(); i++) {
            if (mPlayList.get(i).equals(mCurrentSong)) {
                if (i < 3)
                    mBottomPlayRecyclerView.scrollToPosition(0);
                else {
                    //这里使用LinearLayoutManager的定位方法，因为到列表最后几项，
                    //scrolltoposition方法定到的位置完全不准
                    ((LinearLayoutManager) mBottomPlayRecyclerView.getLayoutManager())
                            .scrollToPositionWithOffset(i - 3, 0);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public static PlayListBottomFragment newInstance() {
        return new PlayListBottomFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_dialog_play_list, null);
        mUnbinder = ButterKnife.bind(this, view);
        dialog.setContentView(view);

        //修改dialog默认高度
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(view.getMinimumHeight());
//        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return dialog;
    }

    private class PlayListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mTitle;
        private final ImageView mPlayingImg;
        private final SpannableStringBuilder mBuilder;

        PlayListHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.bottom_item_title_text_view);
            mPlayingImg = (ImageView) itemView.findViewById(R.id.playing_image_view);
            itemView.setOnClickListener(this);
            mBuilder = new SpannableStringBuilder();
        }

        void BindData(int position) {
            mBuilder.append(mPlayList.get(position).getTitle()).
                    append(TextUtils.formatString(getActivity(),
                            " - " + mPlayList.get(position).getArtist(),
                            R.style.SmallTextAppearance));
            mTitle.setText(mBuilder);
            mBuilder.delete(0, mBuilder.length());
            if (mCurrentSong.equals(mPlayList.get(position))) {
                mPlayingImg.setVisibility(View.VISIBLE);
                mTitle.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            } else {
                mPlayingImg.setVisibility(View.GONE);
                mTitle.setTextColor(getResources().getColor(R.color.song_title));
            }
        }

        @Override
        public void onClick(View v) {
            mCurrentSong = mPlayList.get(getAdapterPosition());
            mApp.playInList(getAdapterPosition());
            mBottomPlayRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private class PlayListAdapter extends RecyclerView.Adapter<PlayListHolder> {

        @Override
        public PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_bottom_sheet_recycler_view, parent, false);

            //RecyclerView的水波纹点击效果
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            view.setBackgroundResource(typedValue.resourceId);

            return new PlayListHolder(view);
        }

        @Override
        public void onBindViewHolder(PlayListHolder holder, int position) {
            holder.BindData(position);
        }

        @Override
        public int getItemCount() {
            return mPlayList.size();
        }
    }
}

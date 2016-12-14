package com.howshea.artisanmusic.UI.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.base.baseutils.ToastUtil;
import com.howshea.artisanmusic.model.SongLab;
import com.howshea.artisanmusic.model.SongList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.howshea.artisanmusic.UI.dialog
 * FileName：   SongListsDialog
 * Created by haipo on 2016/12/9.
 */

public class SongListsDialog extends DialogFragment {

    @BindView(R.id.dialog_song_lists_lv)
    ListView mListView;

    private static final String ARG_ID = "song_id";
    private Unbinder mUnbinder;
    private long mSongId;
    private ArrayList<SongList> mCurrentList;

    public static SongListsDialog newInstance(long song_id) {

        Bundle args = new Bundle();
        args.putLong(ARG_ID, song_id);
        SongListsDialog fragment = new SongListsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongId = getArguments().getLong(ARG_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_song_lists, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addToList(mSongId, mCurrentList.get(position));
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设定dialog宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        initlist();

    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    private void addToList(final long songId, final SongList songList) {
        Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        boolean isSuccess = SongLab.get().addToList(songId, songList);
                        subscriber.onNext(isSuccess);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isSuccess) {
                        if (isSuccess) {
                            dismiss();
                            ToastUtil.showShort("添加成功");
                        } else {
                            ToastUtil.showShort("已在该歌单中！");
                        }
                    }
                });
    }

    private void initlist() {
        Observable
                .create(new Observable.OnSubscribe<ArrayList<SongList>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<SongList>> subscriber) {
                        ArrayList<SongList> songlists = SongLab.get().getSonglists();
                        subscriber.onNext(songlists);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<SongList>>() {
                    @Override
                    public void call(ArrayList<SongList> songLists) {
                        if (songLists != null) {
                            mCurrentList = songLists;
                            mListView.setAdapter(new SongListViewAdapter(songLists));
                        }
                    }
                });
    }

    private static class SongListViewAdapter extends BaseAdapter {

        private List<SongList> mSongLists;

        public SongListViewAdapter(List<SongList> songLists) {
            mSongLists = songLists;
        }

        @Override
        public int getCount() {
            return mSongLists.size();
        }

        @Override
        public SongList getItem(int i) {
            return mSongLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_frg_play_lists_list, null);
                holder.mListHeaderTextView = (TextView) view.findViewById(R.id.list_header_text_view);
                holder.mListDecTextView = (TextView) view.findViewById(R.id.list_dec_textView);
                holder.mListCountTextView = (TextView) view.findViewById(R.id.list_count_text_view);
                holder.mListMenuImageView = (ImageView) view.findViewById(R.id.list_menu_image_view);
                view.setTag(holder);
            }
            SongList list = mSongLists.get(i);
            holder.mListHeaderTextView.setText(list.getTitle().substring(0, 1));
            holder.mListDecTextView.setText(list.getTitle());
            holder.mListCountTextView.setText(list.getSongCount() + " 首");
            holder.mListMenuImageView.setVisibility(View.GONE);

            return view;
        }


        static class ViewHolder {
            TextView mListHeaderTextView;
            TextView mListDecTextView;
            TextView mListCountTextView;
            ImageView mListMenuImageView;
        }
    }
}

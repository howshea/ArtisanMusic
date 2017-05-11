package com.howshea.artisanmusic.UI.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.model.SongList;

import java.util.List;

/**
 * PackageName: com.howshea.artisanmusic.UI.adapter
 * FileName：   SongListViewAdapter
 * Created by haipo on 2016/12/10.
 */


public class SongListViewAdapter extends BaseAdapter {

    public interface Callback {
        void expandPopupMenu(SongList songList, View view);
    }

    private Callback mCallback;

    private List<SongList> mSongLists;

    public SongListViewAdapter(List<SongList> songLists, Fragment fragment) {
        mSongLists = songLists;
        mCallback = (Callback) fragment;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
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
        holder.mListMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.expandPopupMenu(mSongLists.get(i), view);
            }
        });

        return view;
    }


    private static class ViewHolder {
        TextView mListHeaderTextView;
        TextView mListDecTextView;
        TextView mListCountTextView;
        ImageView mListMenuImageView;
    }
}

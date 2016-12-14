package com.howshea.artisanmusic.UI.IView;

import com.howshea.artisanmusic.base.basemvp.IBaseView;
import com.howshea.artisanmusic.model.SongList;

import java.util.List;

/**
 * PackageName: com.haipo.artisanmusic.app
 * FileName：   ISongListsView
 * Created by haipo on 2016/11/8.
 */

public interface ISongListsView extends IBaseView {
    void setAllSongs(int count);

    void setFavorite(int count);

    void setAdapter(List<SongList> songLists);

    void toSonglist(String title);

    void setListCount(int size);

    void showProgressDialog();

    void cancelProgressDialog();
}

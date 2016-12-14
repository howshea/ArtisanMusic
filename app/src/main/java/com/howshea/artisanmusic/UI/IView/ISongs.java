package com.howshea.artisanmusic.UI.IView;

import com.howshea.artisanmusic.base.basemvp.IBaseView;
import com.howshea.artisanmusic.model.Song;

import java.util.ArrayList;

/**
 * PackageName: com.haipo.artisanmusic.UI.IView
 * FileName：   ISongs
 * Created by haipo on 2016/11/16.
 */

public interface ISongs extends IBaseView {
    void setAdapter(ArrayList<Song> songs);
    void closeLoading();
    void refreshAdapter();
    void close();
}

package com.howshea.artisanmusic.UI.IView;

import android.widget.ImageView;

import com.howshea.artisanmusic.base.basemvp.IBaseView;

/**
 * Created by howshea
 * on 2017/5/15.
 */

public interface IHomePageView  extends IBaseView{
    ImageView setImage();
    void jumpToMainActivity();
}

package com.howshea.artisanmusic.UI.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.IHomePageView;
import com.howshea.artisanmusic.base.basemvp.BaseActivtiy;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.presenter.HomePagePresenter;

import butterknife.BindView;


public class HomePageActivity extends BaseActivtiy<IHomePageView, HomePagePresenter> implements IHomePageView {


    @BindView(R.id.home_page_background)
    ImageView mHomePageBackground;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home_page;
    }

    @Override
    protected HomePagePresenter getPresenter() {
        return new HomePagePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getData();
    }

    @Override
    public ImageView setImage() {
        return mHomePageBackground;
    }
}

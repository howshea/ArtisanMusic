package com.howshea.artisanmusic.UI.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.howshea.artisanmusic.R;
import com.howshea.artisanmusic.UI.IView.IHomePageView;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.basemvp.BaseActivtiy;
import com.howshea.artisanmusic.event.AppExitEvent;
import com.howshea.artisanmusic.presenter.HomePagePresenter;
import com.howshea.artisanmusic.utils.SystemUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        AppApplication.getMyEventBus().register(this);
        mPresenter.afterTwoSecToJump();
        mPresenter.getData(SystemUtil.getScreenWidth(this),SystemUtil.getScreenHeight(this));
    }

    @Override
    protected void onDestroy() {
        AppApplication.getMyEventBus().unregister(this);
        super.onDestroy();
    }

    @Override
    public ImageView setImage() {
        return mHomePageBackground;
    }

    @Override
    public void jumpToMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.newIntent(HomePageActivity.this));
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(AppExitEvent event) {
        finish();
    }
}

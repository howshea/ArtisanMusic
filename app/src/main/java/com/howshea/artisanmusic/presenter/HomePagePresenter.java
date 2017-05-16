package com.howshea.artisanmusic.presenter;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.howshea.artisanmusic.UI.IView.IHomePageView;
import com.howshea.artisanmusic.base.baseapp.BaseApplication;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.model.HomePage;
import com.howshea.artisanmusic.network.HttpRequest;

import rx.Subscriber;

/**
 * Created by howshea
 * on 2017/5/16.
 */

public class HomePagePresenter extends BasePresenter<IHomePageView> {

    private HomePage.CreativesBean creativesBean;

    public HomePagePresenter(IHomePageView view) {
        super(view);
    }

    public void getData() {
        HttpRequest.getInstance().getHomePage()
                .subscribe(new Subscriber<HomePage>() {
                    @Override
                    public void onNext(HomePage homePage) {
                        creativesBean = homePage.getCreatives().get(0);
                        LogUtils.logd(creativesBean.getUrl()+"我的天呐");

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {
                        if (isViewAttached()) {
                            showData();
                        }
                    }

                });
    }

    private void showData() {
        Glide.with(BaseApplication.getAppContext())
                .load(creativesBean.getUrl())
                .asBitmap()
                .into(getView().setImage());

    }
}

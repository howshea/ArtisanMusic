package com.howshea.artisanmusic.presenter;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.howshea.artisanmusic.UI.IView.IHomePageView;
import com.howshea.artisanmusic.base.baseapp.BaseApplication;
import com.howshea.artisanmusic.base.basemvp.BasePresenter;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.model.HomePage;
import com.howshea.artisanmusic.network.HttpRequest;

import java.util.Timer;
import java.util.TimerTask;

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
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                    }

                    @Override
                    public void onCompleted() {
                        if (isViewAttached()) {
                            showData();
                        } else {
                            unsubscribe();
                        }
                    }

                });
    }


    public void afterTwoSecToJump() {
        Timer timer = new Timer();// 实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                if (isViewAttached()) {
                    getView().jumpToMainActivity();
                }
                this.cancel();
            }
        }, 2000);// 这里百毫秒
    }

    private void showData() {
        Glide.with(BaseApplication.getAppContext())
                .load(creativesBean.getUrl())
                .asBitmap()
                .into(getView().setImage());
    }
}

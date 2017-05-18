package com.howshea.artisanmusic.presenter;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

    public HomePagePresenter(IHomePageView view) {
        super(view);
    }

    public void getData(int width,int height) {
        Glide.with(BaseApplication.getAppContext())
                .load("https://bing.ioliu.cn/v1?&w="+width+"&h="+height)
//                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().setImage());
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

}

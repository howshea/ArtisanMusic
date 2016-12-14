package com.howshea.artisanmusic.base.basemvp;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * PackageName: com.haipo.artisanmusic.base.basemvp
 * FileName：   BaseActivtiy
 * Created by haipo on 2016/11/9.
 */

public abstract class BaseActivtiy<V extends IBaseView, P extends BasePresenter<V>>  extends AppCompatActivity {

    protected P mPresenter;

    /**
     * @return 布局文件
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * @return presenter对象
     */
    protected abstract P getPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mPresenter=getPresenter();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }
}

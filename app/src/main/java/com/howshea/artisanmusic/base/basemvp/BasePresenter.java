package com.howshea.artisanmusic.base.basemvp;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * PackageName: com.haipo.artisanmusic.base.basemvp
 * FileName：   BasePresenter
 * Created by haipo on 2016/11/9.
 * presenter基础类，持有一个弱引用，防止内存泄漏
 */

public abstract class BasePresenter <V extends IBaseView> {

    private Reference<V> mVReference;

    //建立一个view的弱引用
    public BasePresenter(V view){
        mVReference=new WeakReference<V>(view);
    }


    protected V getView(){
        return mVReference.get();
    }

    //double check，判断引用是否已经被回收
    public boolean isViewAttached(){
        return mVReference!=null&&mVReference.get()!=null;
    }

    //解除Presenter对View的引用
    public void detachView(){
        if (mVReference != null) {
            mVReference.clear();
            mVReference=null;
        }
    }
}

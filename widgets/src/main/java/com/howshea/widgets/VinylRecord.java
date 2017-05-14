package com.howshea.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * PackageName: com.haipo.widgets
 * FileName：   VinylRecord
 * Created by haipo on 2016/12/1.
 */

public class VinylRecord extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

//    private int mWidth;

    private boolean mIsDrawing;
    private Bitmap mTopLine;
    private Bitmap mNeedle;
    private Bitmap mRecordBg;
    private Bitmap mVinyl;
    private Matrix mVinylMatrix = new Matrix();
    private Matrix mCoverMatrix = new Matrix();
    private Matrix mNeedleMatrix = new Matrix();
    private Bitmap mCover;

    private float mCoverCenterX;
    private float mCoverCenterY;
    private float mRotateAngle = 0;
    private float mNeedleAngle = 0;

    private boolean mIsPlaying = true;
    private Bitmap mCoverDefault;
    private Thread mDrawThread;

    private static final String tag = "VinylRecord";
    private float mVinylX;
    private float mVinylY;
    private float mCoverX;
    private float mCoverY;
    private float mToplineCenterX;
    private int mCoverWidth;
    private int mNeedleX;
    private int mNeedleY;
    private int mNeedleCenterX;
    private int mRecordBgX;
    private Paint mPaint = new Paint();

    public VinylRecord(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public VinylRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VinylRecord(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
//        this.setKeepScreenOn(true);
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        Log.i(tag, "初始化");
//        initBitmap();
        initBitmap();
    }

    private void initBitmap() {

        mTopLine = BitmapFactory.decodeResource(getResources(), R.drawable.ic_top_line);
        mNeedle = BitmapFactory.decodeResource(getResources(), R.drawable.ic_needle);
        mRecordBg = BitmapFactory.decodeResource(getResources(), R.drawable.recode_bg);
        mVinyl = BitmapFactory.decodeResource(getResources(), R.drawable.vinyl);
        mCoverDefault = BitmapFactory.decodeResource(getResources(), R.drawable.cover_default);


    }

    private void initCoordinate() {
        mCoverCenterX = getWidth() / 2f;
        mCoverCenterY = mNeedle.getHeight() / 50.0f * 24 + mVinyl.getHeight() / 2f;
        mVinylX = mCoverCenterX - mVinyl.getWidth() / 2f;
        mVinylY = mCoverCenterY - mVinyl.getHeight() / 2f;
        mCoverX = mCoverCenterX - mCoverDefault.getWidth() / 2f;
        mCoverY = mCoverCenterY - mCoverDefault.getHeight() / 2f;
        mToplineCenterX = -mTopLine.getWidth() / 2f;
        mCoverWidth = mCoverDefault.getWidth();
        mNeedleX = getWidth() / 2 - mNeedle.getWidth() / 6;
        mNeedleY = -mNeedle.getWidth() / 6;
        mNeedleCenterX = getWidth() / 2;
        mRecordBgX = -mRecordBg.getWidth() / 2;
    }

    @SuppressWarnings("unused")
    private void recycleBitmap() {
        if (mTopLine != null) {
            mTopLine.recycle();
        }
        if (mNeedle != null) {
            mNeedle.recycle();
        }
        if (mRecordBg != null) {
            mRecordBg.recycle();
        }
        if (mVinyl != null) {
            mVinyl.recycle();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        initBitmap();
        Log.i(tag, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(tag, "surfaceChanged");
        mIsDrawing = true;
//        mWidth = getWidth();
        initCoordinate();
        if (mDrawThread == null) {
            mDrawThread = new Thread(this);
            mDrawThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
        mDrawThread = null;
    }


    @Override
    public void run() {
        Log.i(tag, "run");
        long t;
        while (mIsDrawing) {
            t = System.currentTimeMillis();
            doDraw();
            try {
                Thread.sleep(Math.max(0, 16 - System.currentTimeMillis() + t));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void doDraw() {
        if (mIsDrawing) {
            try {
                mCanvas = mHolder.lockCanvas();
                if (mIsPlaying) {
                    mRotateAngle += 0.45f;
                    if (mRotateAngle >= 360f)
                        mRotateAngle = 0;
                }
                if (mIsPlaying && mNeedleAngle < 0 && mNeedleAngle != 26.5) {
                    mNeedleAngle += 1.5f;

                } else if (!mIsPlaying && mNeedleAngle > -25f) {
                    mNeedleAngle -= 1f;
                }
                draw(mCanvas);
            } catch (Exception e) {
                Log.e(tag, "异常", e);
            } finally {
                if (mCanvas != null)
                    mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

//        initBitmap();
//        initCoordinate();

//
//        if (mIsPlaying) {
//            if (mRotateAngle > 360) {
//                mRotateAngle = 0;
//                mVinylMatrix.reset();
//                mCoverMatrix.reset();
//                mVinylMatrix.setTranslate(
//                        mCoverCenterX - mVinyl.getWidth() / 2f,
//                        mCoverCenterY - mVinyl.getHeight() / 2f);
//                mCoverMatrix.setTranslate(
//                        mCoverCenterX - mCoverDefault.getWidth() / 2f,
//                        mCoverCenterY - mCoverDefault.getHeight() / 2f);
//            }
//            mVinylMatrix.postRotate(0.45f, mCoverCenterX, mCoverCenterY);
//            mCoverMatrix.postRotate(0.45f, mCoverCenterX, mCoverCenterY);
//            mRotateAngle += 0.45f;
//        }


//        if (mIsPlaying && mNeedleAngle < 0 && mNeedleAngle != 26.5) {
//            mNeedleMatrix.postRotate(1.5f, mWidth / 2, 0);
//            mNeedleAngle += 1.5f;
//
//        } else if (!mIsPlaying && mNeedleAngle > -25f) {
//            mNeedleMatrix.postRotate(-1f, mWidth / 2, 0);
//            mNeedleAngle -= 1f;
//        }

        //绘制透明圆形背景
        canvas.save();
        canvas.translate(mCoverCenterX, mCoverCenterY);
        //noinspection SuspiciousNameCombination
        canvas.drawBitmap(mRecordBg, mRecordBgX, mRecordBgX, mPaint);
        canvas.restore();

        //绘制封面
        mCoverMatrix.setRotate(mRotateAngle, mCoverCenterX, mCoverCenterY);
        mCoverMatrix.preTranslate(mCoverX, mCoverY);
        if (mCover != null) {
            //noinspection SuspiciousNameCombination
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(mCover, mCoverWidth, mCoverWidth, true),
                    mCoverMatrix,
                    mPaint);
        } else {
            canvas.drawBitmap(mCoverDefault, mCoverMatrix, mPaint);
        }


        //绘制黑胶
        mVinylMatrix.setRotate(mRotateAngle, mCoverCenterX, mCoverCenterY);
        mVinylMatrix.preTranslate(mVinylX, mVinylY);
        canvas.drawBitmap(mVinyl, mVinylMatrix, mPaint);
//
        //绘制磁头
        mNeedleMatrix.setRotate(mNeedleAngle, mNeedleCenterX, 0);
        mNeedleMatrix.preTranslate(mNeedleX, mNeedleY);
        canvas.drawBitmap(mNeedle, mNeedleMatrix, mPaint);

        //绘制虚线，移动坐标系，从中心往两侧绘制
        canvas.save();
        canvas.translate(getWidth() / 2, 0);
        canvas.drawBitmap(mTopLine, mToplineCenterX, 0, mPaint);
        canvas.restore();


    }


    public void setCoverBitmap(final String path) {
        Observable
                .create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        Bitmap bitmap = null;
                        if (!TextUtils.isEmpty(path)) {
                            bitmap = BitmapFactory.decodeFile(path);
                        }
                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        mRotateAngle = 0;
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        mCover = bitmap;
                        mRotateAngle = 0;
                        Log.i(tag, "getCoverBitmap");
                    }
                });

    }

    public void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
//        invalidate();
    }

    public void switchSong() {
        mIsPlaying = true;
    }

    @SuppressWarnings("unused")
    public void pause() {
        mIsPlaying = false;
    }
}

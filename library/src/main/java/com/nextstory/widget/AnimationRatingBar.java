package com.nextstory.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * 애니메이션 레이팅바
 *
 * @author willy (2017.5.5)
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
public class AnimationRatingBar extends BaseRatingBar {
    protected Handler mHandler;
    protected Runnable mRunnable;
    protected String mRunnableToken = UUID.randomUUID().toString();

    protected AnimationRatingBar(Context context) {
        super(context);
    }

    protected AnimationRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected AnimationRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void postRunnable(Runnable runnable, long ANIMATION_DELAY) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }

        long timeMillis = SystemClock.uptimeMillis() + ANIMATION_DELAY;
        mHandler.postAtTime(runnable, mRunnableToken, timeMillis);
    }
}

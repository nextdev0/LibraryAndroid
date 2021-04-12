package com.nextstory.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.R;
import com.nextstory.widget.util.ratingbar.PartialView;

/**
 * 확대 효과 레이팅바
 *
 * @author willy (2017.5.5)
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ScaleRatingBar extends AnimationRatingBar {
    // Control animation speed
    private static final long ANIMATION_DELAY = 15;

    public ScaleRatingBar(Context context) {
        super(context);
    }

    public ScaleRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void emptyRatingBar() {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of
        // sync
        if (mRunnable != null) {
            mHandler.removeCallbacksAndMessages(mRunnableToken);
        }

        long delay = 0;
        for (final PartialView view : mPartialViews) {
            mHandler.postDelayed(view::setEmpty, delay += 5);
        }
    }

    @Override
    protected void fillRatingBar(float rating) {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of
        // sync
        if (mRunnable != null) {
            mHandler.removeCallbacksAndMessages(mRunnableToken);
        }

        for (final PartialView partialView : mPartialViews) {
            final int ratingViewId = (int) partialView.getTag();
            final double maxIntOfRating = Math.ceil(rating);

            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty();
                continue;
            }

            mRunnable = getAnimationRunnable(rating, partialView, ratingViewId, maxIntOfRating);
            postRunnable(mRunnable, ANIMATION_DELAY);
        }
    }

    @NonNull
    private Runnable getAnimationRunnable(float rating,
                                          PartialView partialView,
                                          int ratingViewId,
                                          double maxIntOfRating) {
        return () -> {
            if (ratingViewId == maxIntOfRating) {
                partialView.setPartialFilled(rating);
            } else {
                partialView.setFilled();
            }

            if (ratingViewId == rating) {
                Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
                partialView.startAnimation(scaleUp);
                partialView.startAnimation(scaleDown);
            }
        };
    }
}

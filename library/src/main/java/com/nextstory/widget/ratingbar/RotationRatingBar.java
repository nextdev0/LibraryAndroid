package com.nextstory.widget.ratingbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.R;

/**
 * 회전 효과 레이팅바
 *
 * @author nappannda (2017.5.16)
 * @version 1.0
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
public class RotationRatingBar extends AnimationRatingBar {
    // Control animation speed
    private static final long ANIMATION_DELAY = 15;

    public RotationRatingBar(Context context) {
        super(context);
    }

    public RotationRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void emptyRatingBar() {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        if (mRunnable != null) {
            mHandler.removeCallbacksAndMessages(mRunnableToken);
        }

        long delay = 0;
        for (final PartialView partialView : mPartialViews) {
            mHandler.postDelayed(partialView::setEmpty, delay += 5);
        }
    }

    @Override
    protected void fillRatingBar(float rating) {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
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
    private Runnable getAnimationRunnable(float rating, PartialView partialView, int ratingViewId, double maxIntOfRating) {
        return () -> {
            if (ratingViewId == maxIntOfRating) {
                partialView.setPartialFilled(rating);
            } else {
                partialView.setFilled();
            }

            if (ratingViewId == rating) {
                Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
                partialView.startAnimation(rotation);
            }
        };
    }
}

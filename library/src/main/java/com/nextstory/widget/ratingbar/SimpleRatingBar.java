package com.nextstory.widget.ratingbar;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * Created by willy on 2017/5/10.
 */
@SuppressWarnings("UnusedDeclaration")
interface SimpleRatingBar {

    int getNumStars();

    void setNumStars(int numStars);

    float getRating();

    void setRating(float rating);

    int getStarWidth();

    void setStarWidth(@IntRange(from = 0) int starWidth);

    int getStarHeight();

    void setStarHeight(@IntRange(from = 0) int starHeight);

    int getStarPadding();

    void setStarPadding(int ratingPadding);

    void setEmptyDrawable(@NonNull Drawable drawable);

    void setEmptyDrawableRes(@DrawableRes int res);

    void setFilledDrawable(@NonNull Drawable drawable);

    void setFilledDrawableRes(@DrawableRes int res);

    void setMinimumStars(@FloatRange(from = 0.0) float minimumStars);

    boolean isIndicator();

    void setIsIndicator(boolean indicator);

    boolean isScrollable();

    void setScrollable(boolean scrollable);

    boolean isClickable();

    void setClickable(boolean clickable);

    boolean isClearRatingEnabled();

    void setClearRatingEnabled(boolean enabled);

    float getStepSize();

    void setStepSize(@FloatRange(from = 0.1, to = 1.0) float stepSize);


}

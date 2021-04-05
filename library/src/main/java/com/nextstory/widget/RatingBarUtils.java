package com.nextstory.widget;

import android.view.MotionEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author willy (2018.3.8)
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
class RatingBarUtils {
    private static final int MAX_CLICK_DISTANCE = 5;
    private static final int MAX_CLICK_DURATION = 200;
    private static DecimalFormat mDecimalFormat;

    static boolean isClickEvent(float startX, float startY, MotionEvent event) {
        float duration = event.getEventTime() - event.getDownTime();
        if (duration > MAX_CLICK_DURATION) {
            return false;
        }

        float differenceX = Math.abs(startX - event.getX());
        float differenceY = Math.abs(startY - event.getY());
        return !(differenceX > MAX_CLICK_DISTANCE || differenceY > MAX_CLICK_DISTANCE);
    }

    static float calculateRating(PartialView partialView, float stepSize, float eventX) {
        DecimalFormat decimalFormat = RatingBarUtils.getDecimalFormat();
        float ratioOfView = Float.parseFloat(decimalFormat.format((eventX - partialView.getLeft()) / partialView.getWidth()));
        float steps = Math.round(ratioOfView / stepSize) * stepSize;
        return Float.parseFloat(decimalFormat.format((int) partialView.getTag() - (1 - steps)));
    }

    static float getValidMinimumStars(float minimumStars, int numStars, float stepSize) {
        if (minimumStars < 0) {
            minimumStars = 0;
        }

        if (minimumStars > numStars) {
            minimumStars = numStars;
        }

        if (minimumStars % stepSize != 0) {
            minimumStars = stepSize;
        }
        return minimumStars;
    }

    static DecimalFormat getDecimalFormat() {
        if (mDecimalFormat == null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
            symbols.setDecimalSeparator('.');
            mDecimalFormat = new DecimalFormat("#.##", symbols);
        }
        return mDecimalFormat;
    }
}

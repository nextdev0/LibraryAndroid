package com.nextstory.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.R;
import com.nextstory.widget.util.SafeAreaHelper;

/**
 * 시스템 영역이 포함되는 레이아웃
 *
 * @author troy
 * @since 2.0
 */
@SuppressLint("WrongConstant")
public final class SafeArea extends FrameLayout implements SafeAreaHelper.Listener {
    private final Rect currentSystemInsets = new Rect();
    private final Rect realPaddingRect = new Rect();

    private boolean isLeftInsetEnabled;
    private boolean isTopInsetEnabled;
    private boolean isRightInsetEnabled;
    private boolean isBottomInsetEnabled;

    public SafeArea(@NonNull Context context) {
        this(context, null);
    }

    public SafeArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SafeArea(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SafeArea, 0, 0);

        isLeftInsetEnabled = a.getBoolean(R.styleable.SafeArea_safe_leftInsetEnabled, true);
        isTopInsetEnabled = a.getBoolean(R.styleable.SafeArea_safe_topInsetEnabled, true);
        isRightInsetEnabled = a.getBoolean(R.styleable.SafeArea_safe_rightInsetEnabled, true);
        isBottomInsetEnabled = a.getBoolean(R.styleable.SafeArea_safe_bottomInsetEnabled, true);

        if (a.hasValue(R.styleable.SafeArea_android_padding)) {
            setPadding(
                    a.getDimensionPixelSize(R.styleable.SafeArea_android_padding, 0),
                    a.getDimensionPixelSize(R.styleable.SafeArea_android_padding, 0),
                    a.getDimensionPixelSize(R.styleable.SafeArea_android_padding, 0),
                    a.getDimensionPixelSize(R.styleable.SafeArea_android_padding, 0));
        } else {
            int paddingLeft = a.getDimensionPixelSize(
                    R.styleable.SafeArea_android_paddingLeft,
                    (getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                            ? a.getDimensionPixelSize(R.styleable.SafeArea_android_paddingEnd, 0)
                            : a.getDimensionPixelSize(
                            R.styleable.SafeArea_android_paddingStart,
                            0));
            int paddingTop = a.getDimensionPixelSize(
                    R.styleable.SafeArea_android_paddingTop,
                    0);
            int paddingRight = a.getDimensionPixelSize(
                    R.styleable.SafeArea_android_paddingRight,
                    (getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                            ? a.getDimensionPixelSize(R.styleable.SafeArea_android_paddingStart, 0)
                            : a.getDimensionPixelSize(
                            R.styleable.SafeArea_android_paddingEnd,
                            0));
            int paddingBottom = a.getDimensionPixelSize(
                    R.styleable.SafeArea_android_paddingBottom,
                    0);
            if (a.hasValue(R.styleable.SafeArea_android_paddingHorizontal)) {
                int paddingHorizontal = a.getDimensionPixelSize(
                        R.styleable.SafeArea_android_paddingHorizontal,
                        0);
                paddingLeft = paddingHorizontal;
                paddingRight = paddingHorizontal;
            }
            if (a.hasValue(R.styleable.SafeArea_android_paddingVertical)) {
                int paddingVertical = a.getDimensionPixelSize(
                        R.styleable.SafeArea_android_paddingVertical,
                        0);
                paddingTop = paddingVertical;
                paddingBottom = paddingVertical;
            }
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        SafeAreaHelper.addListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        SafeAreaHelper.removeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        realPaddingRect.set(left, top, right, bottom);
        requestLayout();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        realPaddingRect.set(
                (getLayoutDirection() == LAYOUT_DIRECTION_RTL) ? end : start,
                top,
                (getLayoutDirection() == LAYOUT_DIRECTION_RTL) ? start : end,
                bottom);
        requestLayout();
    }

    @Override
    public void requestLayout() {
        if (currentSystemInsets != null && realPaddingRect != null) {
            super.setPadding(
                    (isLeftInsetEnabled ? currentSystemInsets.left : 0)
                            + ((getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                            ? realPaddingRect.right : realPaddingRect.left),
                    (isTopInsetEnabled ? currentSystemInsets.top : 0) + realPaddingRect.top,
                    (isRightInsetEnabled ? currentSystemInsets.right : 0)
                            + ((getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                            ? realPaddingRect.left : realPaddingRect.right),
                    (isBottomInsetEnabled ? currentSystemInsets.bottom : 0)
                            + realPaddingRect.bottom);
        }
        super.requestLayout();
    }

    @Override
    public int getPaddingTop() {
        return realPaddingRect.top;
    }

    @Override
    public int getPaddingBottom() {
        return realPaddingRect.bottom;
    }

    @Override
    public int getPaddingLeft() {
        return realPaddingRect.left;
    }

    @Override
    public int getPaddingStart() {
        return (getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                ? realPaddingRect.right : realPaddingRect.left;
    }

    @Override
    public int getPaddingRight() {
        return realPaddingRect.right;
    }

    @Override
    public int getPaddingEnd() {
        return (getLayoutDirection() == LAYOUT_DIRECTION_RTL)
                ? realPaddingRect.left : realPaddingRect.right;
    }

    @Override
    public void onSafeAreaChanged(Rect fitSystemInsets, Rect systemInsets) {
        if (!currentSystemInsets.equals(fitSystemInsets)) {
            currentSystemInsets.set(fitSystemInsets);
            requestLayout();
        }
    }

    public boolean isLeftInsetEnabled() {
        return isLeftInsetEnabled;
    }

    public void setLeftInsetEnabled(boolean leftInsetEnabled) {
        isLeftInsetEnabled = leftInsetEnabled;
        requestLayout();
    }

    public boolean isTopInsetEnabled() {
        return isTopInsetEnabled;
    }

    public void setTopInsetEnabled(boolean topInsetEnabled) {
        isTopInsetEnabled = topInsetEnabled;
        requestLayout();
    }

    public boolean isRightInsetEnabled() {
        return isRightInsetEnabled;
    }

    public void setRightInsetEnabled(boolean rightInsetEnabled) {
        isRightInsetEnabled = rightInsetEnabled;
        requestLayout();
    }

    public boolean isBottomInsetEnabled() {
        return isBottomInsetEnabled;
    }

    public void setBottomInsetEnabled(boolean bottomInsetEnabled) {
        isBottomInsetEnabled = bottomInsetEnabled;
        requestLayout();
    }
}

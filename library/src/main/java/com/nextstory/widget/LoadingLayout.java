package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.nextstory.R;

/**
 * 로딩 레이아웃
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class LoadingLayout extends FrameLayout {
    private final int loadingViewId;
    private boolean isLoading;
    private View loadingView = null;

    public LoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null) {
            loadingViewId = NO_ID;
            isLoading = false;
        } else {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0);
            loadingViewId = a.getResourceId(
                    R.styleable.LoadingLayout_loadingViewId,
                    NO_ID);
            isLoading = a.getBoolean(
                    R.styleable.LoadingLayout_loading,
                    false);
            a.recycle();
        }

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * 로딩 유무 설정
     *
     * @param v     뷰
     * @param value 로딩 유무
     * @see BaseRatingBar
     */
    @BindingAdapter("loading")
    public static void setRating(@NonNull LoadingLayout v, boolean value) {
        v.setLoading(value);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (loadingView == null && loadingViewId != NO_ID) {
            loadingView = findViewById(loadingViewId);
        }
        if (loadingViewId == NO_ID) {
            loadingView = inflate(getContext(), R.layout.view_simple_loading, null);
            addView(loadingView);
        }
        if (loadingView != null) {
            loadingView.bringToFront();
            loadingView.setVisibility(isLoading ? VISIBLE : GONE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (loadingViewId != NO_ID) {
            loadingView = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (isLoading && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? VISIBLE : GONE);
        }
    }
}

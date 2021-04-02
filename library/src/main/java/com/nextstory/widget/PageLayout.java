package com.nextstory.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nextstory.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 페이지 레이아웃
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class PageLayout extends ViewPager {
    private final int startIndex;
    private final boolean isScrollable;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public PageLayout(@NonNull Context context) {
        this(context, null);
    }

    public PageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs == null) {
            startIndex = 0;
            isScrollable = true;
        } else {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageLayout, 0, 0);
            startIndex = a.getInteger(R.styleable.PageLayout_pl_startIndex, 0);
            isScrollable = a.getBoolean(R.styleable.PageLayout_pl_scrollable, true);
            a.recycle();
        }
    }

    @Nullable
    @Override
    public final PagerAdapter getAdapter() {
        return super.getAdapter();
    }

    @Override
    public final void setAdapter(@Nullable PagerAdapter adapter) {
        // no-op
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!initialized.getAndSet(true)) {
            List<View> views = new ArrayList<>();
            for (int i = 0; i < getChildCount(); i++) {
                views.add(getChildAt(i));
            }
            removeAllViews();
            super.setAdapter(new InternalPagerAdapter(views));
            if (isInEditMode()) {
                setCurrentItem(startIndex, false);
            } else {
                post(() -> setCurrentItem(startIndex, false));
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isScrollable) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isScrollable) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    private static class InternalPagerAdapter extends PagerAdapter {
        private final List<View> views;

        InternalPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = container.getChildAt(position);
            if (v == null) {
                v = views.get(position);
                container.addView(v);
            }
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object o) {
            // no-op
        }
    }
}

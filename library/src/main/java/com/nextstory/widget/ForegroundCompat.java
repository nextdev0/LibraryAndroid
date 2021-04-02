package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.nextstory.R;

import java.util.Objects;

/**
 * foreground 지원 유틸 class
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
final class ForegroundCompat {
    final View view;
    Drawable foregroundDrawable = null;

    public ForegroundCompat(View view) {
        this.view = view;
    }

    public void resolveAttribute(@NonNull Context context,
                                 @Nullable AttributeSet attrs,
                                 int defStyleAttr) {
        Objects.requireNonNull(context);
        if (attrs == null) {
            setForeground(null);
        } else {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.ForegroundView, defStyleAttr, 0);
            Drawable foregroundDrawable =
                    a.getDrawable(R.styleable.ForegroundView_android_foreground);
            if (foregroundDrawable == null) {
                foregroundDrawable = a.getDrawable(R.styleable.ForegroundView_foreground);
            }
            setForeground(foregroundDrawable);
            a.recycle();
        }
        if (view != null) {
            view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (foregroundDrawable != null) {
            foregroundDrawable.setBounds(0, 0, w, h);
        }
    }

    public void draw(Canvas canvas) {
        if (foregroundDrawable != null) {
            foregroundDrawable.draw(canvas);
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        if (foregroundDrawable != null) {
            foregroundDrawable.setHotspot(x, y);
        }
    }

    public void drawableStateChanged() {
        if (view == null) {
            return;
        }
        if (foregroundDrawable != null && foregroundDrawable.isStateful()) {
            foregroundDrawable.setState(view.getDrawableState());
        }
    }

    public void jumpDrawablesToCurrentState() {
        if (foregroundDrawable != null) {
            foregroundDrawable.jumpToCurrentState();
        }
    }

    public Drawable getForeground() {
        return foregroundDrawable;
    }

    public void setForeground(Drawable drawable) {
        if (view == null) {
            return;
        }
        if (foregroundDrawable != drawable) {
            if (foregroundDrawable != null) {
                foregroundDrawable.setCallback(null);
                view.unscheduleDrawable(foregroundDrawable);
            }

            foregroundDrawable = drawable;

            if (foregroundDrawable != null) {
                foregroundDrawable.setBounds(0, 0, view.getWidth(), view.getHeight());
                view.setWillNotDraw(false);
                foregroundDrawable.setCallback(view);
                if (foregroundDrawable.isStateful()) {
                    foregroundDrawable.setState(view.getDrawableState());
                }
            } else {
                view.setWillNotDraw(true);
            }
            view.invalidate();
        }
    }
}

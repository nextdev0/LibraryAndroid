package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.nextstory.R;

/**
 * Shape 배경 구현 클래스
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
final class ShapeDrawableHelper {
    private final LayerDrawable drawable;
    private final GradientDrawable shapeDrawable;
    private int cornerRadius = 0;
    private int strokeWidth = 0;
    @ColorInt
    private int strokeColor = 0;

    public ShapeDrawableHelper(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               @Nullable Drawable backgroundDrawable) {
        this.shapeDrawable = new GradientDrawable();
        this.drawable = new LayerDrawable(new Drawable[]{
                backgroundDrawable == null
                        ? new ColorDrawable(Color.TRANSPARENT)
                        : backgroundDrawable,
                this.shapeDrawable
        });
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeView, 0, 0);
        setCornerRadius(a.getDimensionPixelSize(R.styleable.ShapeView_shape_cornerRadius, 0));
        setStrokeWidth(a.getDimensionPixelSize(R.styleable.ShapeView_shape_strokeWidth, 0));
        setStrokeColor(a.getColor(R.styleable.ShapeView_shape_strokeColor, Color.TRANSPARENT));
        a.recycle();
    }

    public void invalidate() {
        shapeDrawable.setColor(Color.TRANSPARENT);
        shapeDrawable.setCornerRadius(cornerRadius);
        shapeDrawable.setStroke(strokeWidth, strokeColor);
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int value) {
        cornerRadius = value;
        invalidate();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int value) {
        strokeWidth = value;
        invalidate();
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int value) {
        strokeColor = value;
        invalidate();
    }

    public LayerDrawable getDrawable() {
        return drawable;
    }

    public GradientDrawable getShapeDrawable() {
        return shapeDrawable;
    }
}

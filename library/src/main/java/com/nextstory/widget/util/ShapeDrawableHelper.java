package com.nextstory.widget.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.nextstory.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Shape 배경 구현 클래스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ShapeDrawableHelper {
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

    public Drawable getBackground() {
        return drawable.getDrawable(0);
    }

    public void setBackground(Drawable newDrawable) {
        LayerDrawableCompat.setDrawable(drawable, 0, newDrawable);
    }

    /**
     * 호환성 클래스
     */
    static class LayerDrawableCompat {
        static Field layerStateField = null;
        static Method refreshChildPaddingField = null;

        static Field numField = null;
        static Field childrenField = null;
        static Method invalidateCacheMethod = null;

        static Class<?> childDrawableClass = null;
        static Field drawableField = null;

        /**
         * {@link LayerDrawable#setDrawable(int, Drawable)} 구버전 지원
         *
         * @param layerDrawable {@link LayerDrawable}
         * @param index         index
         * @param drawable      드로어블
         */
        public static void setDrawable(LayerDrawable layerDrawable, int index, Drawable drawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                layerDrawable.setDrawable(index, drawable);
                return;
            }

            try {
                if (layerStateField == null) {
                    layerStateField = layerDrawable.getClass().getDeclaredField("mLayerState");
                    layerStateField.setAccessible(true);
                }
                if (numField == null) {
                    Object layerState = layerStateField.get(layerDrawable);
                    numField = Objects.requireNonNull(layerState)
                            .getClass()
                            .getDeclaredField("mNum");
                    numField.setAccessible(true);
                }
                if (childrenField == null) {
                    Object layerState = layerStateField.get(layerDrawable);
                    childrenField = Objects.requireNonNull(layerState)
                            .getClass()
                            .getDeclaredField("mChildren");
                    childrenField.setAccessible(true);
                }
                if (drawableField == null) {
                    Object layerState = layerStateField.get(layerDrawable);
                    Object[] children = (Object[]) childrenField.get(layerState);
                    if (children != null && children.length > 0) {
                        childDrawableClass = children[0].getClass();
                        drawableField = childDrawableClass.getDeclaredField("mDrawable");
                        drawableField.setAccessible(true);
                    }
                }
                if (refreshChildPaddingField == null) {
                    refreshChildPaddingField = layerDrawable.getClass().getDeclaredMethod(
                            "refreshChildPadding",
                            int.class,
                            childDrawableClass);
                    refreshChildPaddingField.setAccessible(true);
                }
                if (invalidateCacheMethod == null) {
                    Object layerState = layerStateField.get(layerDrawable);
                    invalidateCacheMethod = Objects.requireNonNull(layerState)
                            .getClass()
                            .getDeclaredMethod("invalidateCache");
                    invalidateCacheMethod.setAccessible(true);
                }

                if (layerStateField != null
                        && numField != null
                        && childrenField != null
                        && drawableField != null
                        && refreshChildPaddingField != null
                        && invalidateCacheMethod != null) {
                    Object layerState = layerStateField.get(layerDrawable);

                    if (index >= numField.getInt(layerState)) {
                        throw new IndexOutOfBoundsException();
                    }

                    Object[] children =
                            (Object[]) Objects.requireNonNull(childrenField.get(layerState));
                    Object childDrawable = children[index];
                    Drawable internalDrawable = (Drawable) drawableField.get(childDrawable);
                    if (internalDrawable != null) {
                        if (drawable != null) {
                            final Rect bounds = internalDrawable.getBounds();
                            drawable.setBounds(bounds);
                        }

                        internalDrawable.setCallback(null);
                    }

                    if (drawable != null) {
                        drawable.setCallback(layerDrawable);
                    }

                    drawableField.set(childDrawable, drawable);
                    invalidateCacheMethod.invoke(layerState);

                    refreshChildPaddingField.invoke(layerDrawable, index, childDrawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

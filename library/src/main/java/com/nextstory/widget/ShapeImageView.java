package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.nextstory.widget.util.ForegroundDrawableHelper;
import com.nextstory.widget.util.ShapeDrawableHelper;

/**
 * Drawable 생성없이 모양을 구성할 수 있는 {@link android.widget.ImageView}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ShapeImageView extends AppCompatImageView {
    private final ForegroundDrawableHelper foregroundDrawableHelper =
            new ForegroundDrawableHelper(this);
    private final ShapeDrawableHelper shapeDrawableHelper;

    public ShapeImageView(@NonNull Context context) {
        this(context, null);
    }

    public ShapeImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(@NonNull Context context, @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundDrawableHelper.resolveAttribute(context, attrs, defStyleAttr);
        shapeDrawableHelper = new ShapeDrawableHelper(context, attrs, getBackground());
        setBackgroundDrawable(shapeDrawableHelper.getDrawable());
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(
                        shapeDrawableHelper.getDrawable().getBounds(),
                        shapeDrawableHelper.getCornerRadius());
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        shapeDrawableHelper.getShapeDrawable().draw(canvas);

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.drawableHotspotChanged(x, y);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.drawableStateChanged();
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.jumpDrawablesToCurrentState();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        if (foregroundDrawableHelper == null) {
            return super.verifyDrawable(who);
        }
        return super.verifyDrawable(who) || (who == foregroundDrawableHelper.getForeground());
    }

    public Drawable getForeground() {
        return foregroundDrawableHelper.getForeground();
    }

    public void setForeground(Drawable drawable) {
        foregroundDrawableHelper.setForeground(drawable);
    }

    public int getCornerRadius() {
        return shapeDrawableHelper.getCornerRadius();
    }

    public void setCornerRadius(int value) {
        shapeDrawableHelper.setCornerRadius(value);
    }

    public int getStrokeWidth() {
        return shapeDrawableHelper.getStrokeWidth();
    }

    public void setStrokeWidth(int value) {
        shapeDrawableHelper.setStrokeWidth(value);
    }

    public int getStrokeColor() {
        return shapeDrawableHelper.getStrokeColor();
    }

    public void setStrokeColor(int value) {
        shapeDrawableHelper.setStrokeColor(value);
    }
}

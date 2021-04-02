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
import androidx.appcompat.widget.AppCompatEditText;

import com.nextstory.R;

/**
 * Drawable 생성없이 모양을 구성할 수 있는 {@link android.widget.EditText}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ShapeEditText extends AppCompatEditText {
    private final ForegroundCompat foregroundCompat = new ForegroundCompat(this);
    private final ShapeDrawableHelper shapeDrawableHelper;

    public ShapeEditText(@NonNull Context context) {
        this(context, null);
    }

    public ShapeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ShapeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundCompat.resolveAttribute(context, attrs, defStyleAttr);
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
        foregroundCompat.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundCompat.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        foregroundCompat.drawableHotspotChanged(x, y);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        foregroundCompat.drawableStateChanged();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        foregroundCompat.jumpDrawablesToCurrentState();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || (who == foregroundCompat.getForeground());
    }

    public Drawable getForeground() {
        return foregroundCompat.getForeground();
    }

    public void setForeground(Drawable drawable) {
        foregroundCompat.setForeground(drawable);
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

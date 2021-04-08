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
import com.nextstory.widget.util.ForegroundHelper;
import com.nextstory.widget.util.ShapeDrawableHelper;

/**
 * Drawable 생성없이 모양을 구성할 수 있는 {@link android.widget.EditText}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ShapeEditText extends AppCompatEditText {
    private final ForegroundHelper foregroundHelper = new ForegroundHelper(this);
    private final ShapeDrawableHelper shapeDrawableHelper;

    public ShapeEditText(@NonNull Context context) {
        this(context, null);
    }

    public ShapeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ShapeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundHelper.resolveAttribute(context, attrs, defStyleAttr);
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
        foregroundHelper.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        foregroundHelper.drawableHotspotChanged(x, y);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        foregroundHelper.drawableStateChanged();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        foregroundHelper.jumpDrawablesToCurrentState();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || (who == foregroundHelper.getForeground());
    }

    public Drawable getForeground() {
        return foregroundHelper.getForeground();
    }

    public void setForeground(Drawable drawable) {
        foregroundHelper.setForeground(drawable);
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

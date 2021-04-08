package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.widget.util.ForegroundHelper;

/**
 * foreground 지원 {@link RelativeLayout}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public class ForegroundRelativeLayout extends RelativeLayout {
    final ForegroundHelper foregroundHelper = new ForegroundHelper(this);

    public ForegroundRelativeLayout(Context context) {
        this(context, null);
    }

    public ForegroundRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundHelper.resolveAttribute(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        foregroundHelper.draw(canvas);
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
}

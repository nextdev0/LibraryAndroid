package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * foreground 지원 {@link ConstraintLayout}
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public class ForegroundConstraintLayout extends ConstraintLayout {
    final ForegroundCompat foregroundCompat = new ForegroundCompat(this);

    public ForegroundConstraintLayout(Context context) {
        this(context, null);
    }

    public ForegroundConstraintLayout(Context context,
                                      @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundConstraintLayout(Context context,
                                      @Nullable AttributeSet attrs,
                                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundCompat.resolveAttribute(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundCompat.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        foregroundCompat.draw(canvas);
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
}

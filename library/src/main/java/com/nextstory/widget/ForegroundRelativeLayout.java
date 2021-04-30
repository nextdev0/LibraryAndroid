package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.widget.util.ForegroundDrawableHelper;

/**
 * foreground 지원 {@link RelativeLayout}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public class ForegroundRelativeLayout extends RelativeLayout {
    final ForegroundDrawableHelper foregroundDrawableHelper = new ForegroundDrawableHelper(this);

    public ForegroundRelativeLayout(Context context) {
        this(context, null);
    }

    public ForegroundRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundRelativeLayout(Context context,
                                    @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        foregroundDrawableHelper.resolveAttribute(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundDrawableHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        foregroundDrawableHelper.draw(canvas);
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        foregroundDrawableHelper.drawableHotspotChanged(x, y);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        foregroundDrawableHelper.drawableStateChanged();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        foregroundDrawableHelper.jumpDrawablesToCurrentState();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || (who == foregroundDrawableHelper.getForeground());
    }

    public Drawable getForeground() {
        return foregroundDrawableHelper.getForeground();
    }

    public void setForeground(Drawable drawable) {
        foregroundDrawableHelper.setForeground(drawable);
    }
}

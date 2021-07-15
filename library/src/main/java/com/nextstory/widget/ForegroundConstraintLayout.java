package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nextstory.widget.util.ForegroundDrawableHelper;

/**
 * foreground 지원 {@link ConstraintLayout}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public class ForegroundConstraintLayout extends ConstraintLayout {
    final ForegroundDrawableHelper foregroundDrawableHelper = new ForegroundDrawableHelper(this);

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
        foregroundDrawableHelper.resolveAttribute(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (foregroundDrawableHelper != null) {
            foregroundDrawableHelper.draw(canvas);
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
}

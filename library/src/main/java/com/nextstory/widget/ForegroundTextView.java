package com.nextstory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.nextstory.widget.util.ForegroundHelper;

/**
 * foreground 지원 {@link TextView}
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public class ForegroundTextView extends AppCompatTextView {
    final ForegroundHelper foregroundHelper = new ForegroundHelper(this);

    public ForegroundTextView(Context context) {
        this(context, null);
    }

    public ForegroundTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

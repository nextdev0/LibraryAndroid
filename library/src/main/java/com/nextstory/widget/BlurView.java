package com.nextstory.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.nextstory.R;
import com.nextstory.widget.util.blur.Blur;
import com.nextstory.widget.util.blur.BlurImpl;

/**
 * 블러뷰
 *
 * @author troy
 * @since 1.4
 */
@SuppressWarnings("UnusedDeclaration")
public final class BlurView extends View {
    private static final String META_DATA_ENABLED = "com.nextstory.BLUR_VIEW_ENABLED";
    private static int RENDERING_COUNT = 0;
    private static boolean isBlurEnabled = true;

    private final float mDownsampleFactor;
    private final int mOverlayColor;
    private final float mBlurRadius;
    private final Blur mBlurImpl = new BlurImpl();
    private final Paint mPaint = new Paint();
    private final Rect mRectSrc = new Rect();
    private final Rect mRectDst = new Rect();
    private boolean mDirty;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private boolean mIsRendering;
    private View mDecorView;
    private boolean mDifferentRoot;
    private final ViewTreeObserver.OnPreDrawListener mPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    final int[] locations = new int[2];
                    Bitmap oldBmp = mBlurredBitmap;
                    View decor = mDecorView;
                    if (decor != null && isShown() && prepare()) {
                        boolean redrawBitmap = mBlurredBitmap != oldBmp;
                        decor.getLocationOnScreen(locations);
                        int x = -locations[0];
                        int y = -locations[1];

                        getLocationOnScreen(locations);
                        x += locations[0];
                        y += locations[1];

                        // just erase transparent
                        mBitmapToBlur.eraseColor(mOverlayColor & 0xffffff);

                        int rc = mBlurringCanvas.save();
                        mIsRendering = true;
                        RENDERING_COUNT++;
                        try {
                            mBlurringCanvas.scale(1.f * mBitmapToBlur.getWidth() / getWidth(),
                                    1.f * mBitmapToBlur.getHeight() / getHeight());
                            mBlurringCanvas.translate(-x, -y);
                            if (decor.getBackground() != null) {
                                decor.getBackground().draw(mBlurringCanvas);
                            }
                            decor.draw(mBlurringCanvas);
                        } catch (Throwable ignored) {
                        } finally {
                            mIsRendering = false;
                            RENDERING_COUNT--;
                            mBlurringCanvas.restoreToCount(rc);
                        }

                        blur(mBitmapToBlur, mBlurredBitmap);

                        if (redrawBitmap || mDifferentRoot) {
                            invalidate();
                        }
                    }

                    return true;
                }
            };

    public BlurView(Context context) {
        this(context, null);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            ApplicationInfo applicationInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                if (applicationInfo.metaData == null) {
                    isBlurEnabled = true;
                } else {
                    isBlurEnabled = applicationInfo.metaData.getBoolean(META_DATA_ENABLED, true);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            isBlurEnabled = true;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurView);
        mBlurRadius = a.getDimension(
                R.styleable.BlurView_blur_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics));
        mDownsampleFactor = a.getFloat(
                R.styleable.BlurView_blur_downSampleFactor,
                4);
        mOverlayColor = a.getColor(
                R.styleable.BlurView_blur_color,
                0xAAFFFFFF);
        a.recycle();
    }

    private static View getActivityDecorView(Context context) {
        boolean isActivityContext = context instanceof Activity;
        boolean isContextWrapper = context instanceof ContextWrapper;
        for (int i = 0; i < 4 && !isActivityContext && isContextWrapper; i++) {
            context = ((ContextWrapper) context).getBaseContext();
            isActivityContext = context instanceof Activity;
            isContextWrapper = context instanceof ContextWrapper;
        }
        if (context instanceof Activity) {
            return ((Activity) context).getWindow().getDecorView();
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isBlurEnabled) {
            mDecorView = getActivityDecorView(getContext());
            if (mDecorView != null) {
                mDecorView.getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
                mDifferentRoot = mDecorView.getRootView() != getRootView();
                if (mDifferentRoot) {
                    mDecorView.postInvalidate();
                }
            } else {
                mDifferentRoot = false;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (isBlurEnabled) {
            if (mDecorView != null) {
                mDecorView.getViewTreeObserver().removeOnPreDrawListener(mPreDrawListener);
            }
            release();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isBlurEnabled) {
            if (mIsRendering) {
                throw new IllegalStateException("don't draw views");
            }
            if (RENDERING_COUNT <= 0) {
                super.draw(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isBlurEnabled) {
            drawBlurredBitmap(canvas, mBlurredBitmap, mOverlayColor);
        }
    }

    protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap, int overlayColor) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.getWidth();
            mRectSrc.bottom = blurredBitmap.getHeight();
            mRectDst.right = getWidth();
            mRectDst.bottom = getHeight();
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null);
        }
        mPaint.setColor(overlayColor);
        canvas.drawRect(mRectDst, mPaint);
    }

    private void releaseBitmap() {
        if (mBitmapToBlur != null) {
            mBitmapToBlur.recycle();
            mBitmapToBlur = null;
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap.recycle();
            mBlurredBitmap = null;
        }
    }

    protected void release() {
        releaseBitmap();
        mBlurImpl.release();
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    protected boolean prepare() {
        if (mBlurRadius == 0) {
            release();
            return false;
        }

        float downsampleFactor = mDownsampleFactor;
        float radius = mBlurRadius / downsampleFactor;
        if (radius > 25) {
            downsampleFactor = downsampleFactor * radius / 25;
            radius = 25;
        }

        final int width = getWidth();
        final int height = getHeight();

        int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
        int scaledHeight = Math.max(1, (int) (height / downsampleFactor));

        boolean dirty = mDirty;

        if (mBlurringCanvas == null || mBlurredBitmap == null
                || mBlurredBitmap.getWidth() != scaledWidth
                || mBlurredBitmap.getHeight() != scaledHeight) {
            dirty = true;
            releaseBitmap();

            boolean r = false;
            try {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }
                mBlurringCanvas = new Canvas(mBitmapToBlur);

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }

                r = true;
            } catch (OutOfMemoryError e) {
                // Bitmap.createBitmap() may cause OOM error
                // Simply ignore and fallback
            } finally {
                if (!r) {
                    release();
                    return false;
                }
            }
        }

        if (dirty) {
            if (mBlurImpl.prepare(getContext(), mBitmapToBlur, radius)) {
                mDirty = false;
            } else {
                return false;
            }
        }

        return true;
    }

    protected void blur(Bitmap bitmapToBlur, Bitmap blurredBitmap) {
        mBlurImpl.blur(bitmapToBlur, blurredBitmap);
    }
}

package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.nextstory.R;
import com.nextstory.widget.util.ScrollObserverHelper;

import java.util.Objects;

/**
 * 지정한 스크롤의 위치에 따라 높이가 변경되는 레이아웃
 *
 * @author troy
 * @since 2.0
 */
public class ElevatedLayout extends FrameLayout implements ScrollObserverHelper.ScrollObservable {
  private static final int DEFAULT_ELEVATION = 4;

  private final ScrollObserverHelper scrollObserverHelper = new ScrollObserverHelper(this);
  private final int contentViewId;

  private View contentView = null;
  private float currentElevation;

  public ElevatedLayout(Context context) {
    this(context, null);
  }

  public ElevatedLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ElevatedLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.ElevatedLayout, 0, 0);
      contentViewId = a.getResourceId(
        R.styleable.ElevatedLayout_android_content,
        NO_ID);
      currentElevation = a.getDimension(
        R.styleable.ElevatedLayout_android_elevation,
        DEFAULT_ELEVATION * context.getResources().getDisplayMetrics().density + 0.5f);
      a.recycle();
    } else {
      currentElevation =
        DEFAULT_ELEVATION * context.getResources().getDisplayMetrics().density + 0.5f;
      currentElevation = 0f;
      contentViewId = NO_ID;
    }

    setElevation(contentViewId == NO_ID ? currentElevation : 0);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    scrollObserverHelper.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    contentView = null;
    super.onDetachedFromWindow();
  }

  @Override
  public View getContentView() {
    if (contentView == null) {
      contentView = findContentView(getRootView());
    }
    return Objects.requireNonNull(contentView, "contentView == null");
  }

  @Nullable
  private View findContentView(View view) {
    if (view == null) {
      return null;
    }
    if (view.getId() == contentViewId) {
      return view;
    }
    if (view instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup) view;
      for (int i = 0; i < viewGroup.getChildCount(); i++) {
        View childView = viewGroup.getChildAt(i);
        View contentView = findContentView(childView);
        if (contentView != null) {
          return contentView;
        }
      }
    }
    return null;
  }

  @Override
  public void onScrollChanged(int scrollY) {
    setElevation(Math.min(
      scrollY / getResources().getDisplayMetrics().density,
      currentElevation));
  }
}

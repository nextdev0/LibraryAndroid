package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.nextstory.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link DataBindingFlowLayout} 항목
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingFlowLayoutItem extends FrameLayout {
  private final int layoutRes;
  private final String itemBindingName, positionBindingName, callbackBindingName;
  private WeakReference<DataBindingFlowLayout> parent;
  private List<?> items = new ArrayList<>();
  private DataBindingItemCallback callback = null;

  public DataBindingFlowLayoutItem(Context context) {
    this(context, null);
  }

  public DataBindingFlowLayoutItem(Context context,
                                   @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DataBindingFlowLayoutItem(Context context,
                                   @Nullable AttributeSet attrs,
                                   int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (attrs == null) {
      layoutRes = 0;
      itemBindingName = "item";
      positionBindingName = "position";
      callbackBindingName = "callback";
    } else {
      TypedArray a = context.obtainStyledAttributes(
        attrs, R.styleable.DataBindingFlowLayoutItem);
      layoutRes = a.getResourceId(
        R.styleable.DataBindingFlowLayoutItem_layout,
        0);
      if (a.hasValue(R.styleable.DataBindingFlowLayoutItem_itemBindingName)) {
        itemBindingName = a.getString(
          R.styleable.DataBindingFlowLayoutItem_itemBindingName);
      } else {
        itemBindingName = "item";
      }
      if (a.hasValue(R.styleable.DataBindingFlowLayoutItem_positionBindingName)) {
        positionBindingName = a.getString(
          R.styleable.DataBindingFlowLayoutItem_positionBindingName);
      } else {
        positionBindingName = "position";
      }
      if (a.hasValue(R.styleable.DataBindingFlowLayoutItem_callbackBindingName)) {
        callbackBindingName = a.getString(
          R.styleable.DataBindingFlowLayoutItem_callbackBindingName);
      } else {
        callbackBindingName = "callback";
      }
      a.recycle();
    }

    // 디자인 타임에서 표시
    if (isInEditMode() && layoutRes != 0 && getChildCount() == 0) {
      View newChild = View.inflate(getContext(), layoutRes, null);
      addView(newChild, new LayoutParams(-1, -1));
    }
  }

  /**
   * 목록 바인딩
   *
   * @param v    뷰
   * @param list 목록
   */
  @BindingAdapter("items")
  public static void setItems(DataBindingFlowLayoutItem v, List<?> list) {
    if (v != null) {
      v.setItems(list);
    }
  }

  /**
   * 콜백 설정
   *
   * @param v        뷰
   * @param callback 콜백
   */
  @BindingAdapter("onItemCallback")
  public static void setOnItemCallback(DataBindingFlowLayoutItem v,
                                       DataBindingItemCallback callback) {
    if (v != null) {
      v.setCallback(callback);
    }
  }

  @Override
  public void draw(Canvas canvas) {
    if (isInEditMode()) {
      super.draw(canvas);
    }
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    if (isInEditMode()) {
      super.dispatchDraw(canvas);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (isInEditMode()) {
      super.onDraw(canvas);
    }
  }

  public int getLayoutRes() {
    return layoutRes;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  void setParent(DataBindingFlowLayout parent) {
    this.parent = new WeakReference<>(parent);
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  List<?> getItems() {
    if (isInEditMode()) {
      return Collections.singletonList(new Object());
    }
    return items;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  void setItems(List<?> items) {
    this.items = items;
    if (parent != null && parent.get() != null) {
      RecyclerView.Adapter<?> adapter = parent.get().getAdapter();
      if (adapter != null) {
        adapter.notifyDataSetChanged();
      }
    }
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  String getItemBindingName() {
    return itemBindingName;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  String getPositionBindingName() {
    return positionBindingName;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  String getCallbackBindingName() {
    return callbackBindingName;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @Nullable
  DataBindingItemCallback getCallback() {
    return callback;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  void setCallback(@NonNull DataBindingItemCallback callback) {
    this.callback = callback;
  }
}

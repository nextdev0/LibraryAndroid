package com.nextstory.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.nextstory.R;
import com.nextstory.widget.util.flowlayout.Alignment;
import com.nextstory.widget.util.flowlayout.FlowLayoutManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 데이터바인딩 플로우 레이아웃
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingFlowLayout extends RecyclerView {
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  private InternalAdapter adapter;

  public DataBindingFlowLayout(@NonNull Context context) {
    super(context);
    initialize(context, null);
  }

  public DataBindingFlowLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs);
  }

  public DataBindingFlowLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs);
  }

  /**
   * 초기화
   *
   * @param context 컨텍스트
   * @param attrs   AttributeSet
   */
  @SuppressLint("RtlHardcoded")
  private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
    int maxItemsPerLine;
    int gravity;

    if (attrs == null) {
      maxItemsPerLine = 0;
      gravity = GravityCompat.START;
    } else {
      TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.DataBindingFlowLayout);
      maxItemsPerLine = a.getInt(
        R.styleable.DataBindingFlowLayout_maxItemsPerLine,
        0);
      gravity = a.getInt(
        R.styleable.DataBindingFlowLayout_android_gravity,
        GravityCompat.START);
      a.recycle();
    }

    FlowLayoutManager layoutManager = new FlowLayoutManager()
      .maxItemsPerLine(maxItemsPerLine);
    layoutManager.setAutoMeasureEnabled(true);

    if (gravity == Gravity.CENTER) {
      layoutManager.setAlignment(Alignment.CENTER);
    } else {
      if ((gravity & GravityCompat.RELATIVE_LAYOUT_DIRECTION) == 0) {
        layoutManager.setAlignment(gravity == Gravity.LEFT
          ? Alignment.LEFT
          : Alignment.RIGHT);
      } else {
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR) {
          layoutManager.setAlignment(gravity == Gravity.START
            ? Alignment.LEFT
            : Alignment.RIGHT);
        } else {
          layoutManager.setAlignment(gravity == Gravity.END
            ? Alignment.LEFT
            : Alignment.RIGHT);
        }
      }
    }
    super.setLayoutManager(layoutManager);
    super.setHasFixedSize(false);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    ViewGroup.LayoutParams params = getLayoutParams();
    if (params != null) {
      if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        throw new IllegalStateException("layout_height must be wrap_content");
      }
    }

    if (adapter == null) {
      adapter = new InternalAdapter();
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (child instanceof DataBindingFlowLayoutItem) {
          ((DataBindingFlowLayoutItem) child).setParent(this);
        }
        adapter.add(child);
      }
      removeAllViews();
      super.setAdapter(adapter);
    }
  }

  @Nullable
  @Override
  public final Adapter<?> getAdapter() {
    return super.getAdapter();
  }

  @Override
  public final void setAdapter(@Nullable Adapter adapter) {
    // no-op
  }

  @Nullable
  @Override
  public final LayoutManager getLayoutManager() {
    return super.getLayoutManager();
  }

  @Override
  public final void setLayoutManager(@Nullable LayoutManager layout) {
    // no-op
  }

  @Override
  public final void setHasFixedSize(boolean hasFixedSize) {
    // no-op
  }

  @Override
  public final boolean hasFixedSize() {
    return false;
  }

  /**
   * 하단 스크롤 리스너
   */
  public interface OnBottomScrollReachedListener {
    /**
     * 하단 스크롤 도달 시 호출
     */
    void onBottomScrollReached();
  }

  /**
   * 내부 어댑터
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  static class InternalAdapter extends RecyclerView.Adapter<InternalViewHolder> {
    private final List<View> views = new ArrayList<>();

    public InternalAdapter() {
      super();
      setHasStableIds(true);
    }

    @NonNull
    @Override
    public InternalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = views.get(viewType);
      if (view instanceof DataBindingFlowLayoutItem) {
        if (parent.isInEditMode()) {
          return new InternalViewHolder(view);
        }

        DataBindingFlowLayoutItem v = (DataBindingFlowLayoutItem) view;
        ViewDataBinding binding = DataBindingUtil.inflate(
          LayoutInflater.from(parent.getContext()),
          v.getLayoutRes(),
          null,
          false);

        // 임시로 뷰를 숨김
        binding.getRoot().setVisibility(View.GONE);

        return new InternalViewHolder(binding, v);
      } else {
        ViewParent viewParent = view.getParent();
        if (viewParent != null) {
          if (viewParent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) viewParent;
            viewGroup.removeView(view);
          }
        }
        return new InternalViewHolder(view);
      }
    }

    @Override
    public void onBindViewHolder(@NonNull InternalViewHolder holder, int position) {
      if (holder.itemView.isInEditMode()) {
        return;
      }
      if (holder.binding == null) {
        return;
      }

      int viewType = getItemViewType(position);
      int realPosition = getRealPosition(position);
      if (!(views.get(viewType) instanceof DataBindingFlowLayoutItem)) {
        return;
      }

      DataBindingFlowLayoutItem gridItem = (DataBindingFlowLayoutItem) views.get(viewType);
      if (gridItem.getItems().size() < realPosition) {
        return;
      }

      // 임시로 숨겨뒀던 뷰를 표시함
      holder.binding.getRoot().post(() ->
        holder.binding.getRoot().setVisibility(View.VISIBLE));

      if (holder.getItemVarId() != -1) {
        holder.binding.setVariable(
          holder.getItemVarId(),
          gridItem.getItems().get(realPosition));
      }
      if (holder.getPositionVarId() != -1) {
        holder.binding.setVariable(
          holder.getPositionVarId(),
          realPosition);
      }
      if (holder.getCallbackVarId() != -1) {
        holder.binding.setVariable(
          holder.getCallbackVarId(),
          (DataBindingItemCallback) (v, o, i) -> {
            DataBindingItemCallback callback = gridItem.getCallback();
            if (callback != null) {
              callback.onItemCallback(v, o, i);
              return;
            }
            for (View view : views) {
              if (view instanceof DataBindingFlowLayoutItem) {
                DataBindingFlowLayoutItem item =
                  (DataBindingFlowLayoutItem) view;
                if (item.getCallback() != null) {
                  item.getCallback().onItemCallback(v, o, i);
                }
              }
            }
          });
      }
    }

    @Override
    public int getItemCount() {
      int itemCount = 0;
      for (View view : views) {
        if (view instanceof DataBindingFlowLayoutItem) {
          DataBindingFlowLayoutItem gridItem = (DataBindingFlowLayoutItem) view;
          List<?> items = gridItem.getItems();
          itemCount += items.size();
        } else {
          itemCount++;
        }
      }
      return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
      int currentPosition = 0;
      for (int type = 0; type < views.size(); type++) {
        View view = views.get(type);
        int itemCount;
        if (view instanceof DataBindingFlowLayoutItem) {
          DataBindingFlowLayoutItem gridItem = (DataBindingFlowLayoutItem) view;
          List<?> items = gridItem.getItems();
          itemCount = items.size();
        } else {
          itemCount = 1;
        }
        for (int i = 0; i < itemCount; i++) {
          if (currentPosition == position) {
            return type;
          }
          currentPosition++;
        }
      }
      return 0;
    }

    @Override
    public long getItemId(int position) {
      int realPosition = getRealPosition(position);
      int viewType = getItemViewType(position);
      View view = views.get(viewType);
      if (view instanceof DataBindingFlowLayoutItem) {
        DataBindingFlowLayoutItem gridItem = (DataBindingFlowLayoutItem) view;
        List<?> items = gridItem.getItems();
        return items.get(realPosition).hashCode();
      } else {
        return view.hashCode();
      }
    }

    /**
     * 어댑터상 위치에서 실제 뷰 홀더의 모델 위치를 찾아 반환함
     *
     * @param position 어댑터상 위치
     * @return 위치
     */
    public int getRealPosition(int position) {
      int currentPosition = 0;
      for (int type = 0; type < views.size(); type++) {
        View view = views.get(type);
        int itemCount;
        if (view instanceof DataBindingFlowLayoutItem) {
          DataBindingFlowLayoutItem gridItem = (DataBindingFlowLayoutItem) view;
          List<?> items = gridItem.getItems();
          itemCount = items.size();
        } else {
          itemCount = 1;
        }
        for (int i = 0; i < itemCount; i++) {
          if (currentPosition == position) {
            return i;
          }
          currentPosition++;
        }
      }
      return currentPosition;
    }

    /**
     * 추가
     *
     * @param view 뷰
     */
    private void add(View view) {
      views.add(view);
    }
  }

  /**
   * 내부 뷰 홀더
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  static class InternalViewHolder extends ViewHolder {
    private final ViewDataBinding binding;
    private int itemVarId = -1;
    private int positionVarId = -1;
    private int callbackVarId = -1;

    public InternalViewHolder(@NonNull ViewDataBinding binding,
                              @NonNull DataBindingFlowLayoutItem vh) {
      super(binding.getRoot());
      this.binding = binding;
      String packageName = vh.getContext().getPackageName();
      itemVarId = getBindingId(
        packageName.concat(".BR.").concat(vh.getItemBindingName()));
      positionVarId = getBindingId(
        packageName.concat(".BR.").concat(vh.getPositionBindingName()));
      callbackVarId = getBindingId(
        packageName.concat(".BR.").concat(vh.getCallbackBindingName()));
    }

    public InternalViewHolder(@NonNull View itemView) {
      super(createView(itemView));
      this.binding = null;
    }

    private static int getBindingId(String id) {
      String className = id.substring(0, id.lastIndexOf("."));
      String fieldName = id.substring(id.lastIndexOf(".") + 1);
      try {
        Class<?> brClass = Class.forName(className);
        Field varField = brClass.getField(fieldName);
        return (int) Objects.requireNonNull(varField.get(null));
      } catch (Throwable ignore) {
        return -1;
      }
    }

    private static View createView(View v) {
      Context context = v.getContext();
      ViewGroup.MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
      FrameLayout.LayoutParams newParams =
        new FrameLayout.LayoutParams(params.width, params.height);
      newParams.topMargin = params.topMargin;
      newParams.bottomMargin = params.bottomMargin;
      newParams.leftMargin = params.leftMargin;
      newParams.rightMargin = params.rightMargin;
      DataBindingFlowLayoutViewItem newParent = new DataBindingFlowLayoutViewItem(context);
      newParent.addView(v, newParams);
      return newParent;
    }

    public int getItemVarId() {
      return itemVarId;
    }

    public int getPositionVarId() {
      return positionVarId;
    }

    public int getCallbackVarId() {
      return callbackVarId;
    }
  }
}

package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextstory.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * 데이터바인딩 그리드 레이아웃
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingGridLayout extends RecyclerView {
    private int orientation;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private InternalAdapter adapter;

    @BindingAdapter("onBottomScrollReached")
    public static void addOnBottomScrollReachedListener(
            @NonNull final DataBindingGridLayout view,
            @Nullable final OnBottomScrollReachedListener l
    ) {
        view.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView view, int newState) {
                if (!view.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (l != null) {
                        l.onBottomScrollReached();
                    }
                }
            }
        });
    }

    public DataBindingGridLayout(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public DataBindingGridLayout(@NonNull Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public DataBindingGridLayout(@NonNull Context context,
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
    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        boolean reverseLayout;
        int spanCount;

        if (attrs == null) {
            orientation = VERTICAL;
            spanCount = 1;
            reverseLayout = false;
        } else {
            TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.DataBindingGridLayout);
            orientation = a.getInt(
                    R.styleable.DataBindingGridLayout_android_orientation,
                    VERTICAL);
            spanCount = a.getInt(
                    R.styleable.DataBindingGridLayout_spanCount,
                    1);
            reverseLayout = a.getBoolean(
                    R.styleable.DataBindingGridLayout_reverseLayout,
                    false);
            a.recycle();
        }

        GridLayoutManager layoutManager = new InternalLayoutManager(
                context, spanCount, orientation, reverseLayout);
        super.setLayoutManager(layoutManager);
        super.setHasFixedSize(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter == null) {
            adapter = new InternalAdapter();
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof DataBindingGridLayoutItem) {
                    ((DataBindingGridLayoutItem) child).setParent(this);
                }
                adapter.add(child);
            }
            removeAllViews();
            super.setAdapter(adapter);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        if (orientation == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        } else {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
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

    public int getOrientation() {
        return orientation;
    }

    /**
     * 내부 {@link GridLayoutManager}
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class InternalLayoutManager extends GridLayoutManager {
        public InternalLayoutManager(Context context,
                                     int spanCount,
                                     int orientation,
                                     boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        @Override
        public void onLayoutChildren(Recycler recycler, State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException ignore) {
                // no-op
            }
        }
    }

    /**
     * 내부 어댑터
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class InternalAdapter extends RecyclerView.Adapter<InternalViewHolder> {
        private final List<View> views = new ArrayList<>();
        private final Map<View, Integer> spanCounts = new WeakHashMap<>();

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (views.size() == 0) {
                            return 1;
                        }
                        View view = views.get(getItemViewType(position));
                        if (view.getLayoutParams() == null) {
                            return 1;
                        }
                        if (spanCounts.containsKey(view)) {
                            return Objects.requireNonNull(spanCounts.get(view));
                        }
                        return 1;
                    }
                });
            }
        }

        @NonNull
        @Override
        public InternalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = views.get(viewType);
            if (view instanceof DataBindingGridLayoutItem) {
                if (parent.isInEditMode()) {
                    return new InternalViewHolder(view);
                }
                DataBindingGridLayoutItem v = (DataBindingGridLayoutItem) view;
                ViewDataBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        v.getLayoutRes(),
                        null,
                        false);
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
            if (!(views.get(viewType) instanceof DataBindingGridLayoutItem)) {
                return;
            }
            DataBindingGridLayoutItem gridItem = (DataBindingGridLayoutItem) views.get(viewType);
            if (gridItem.getItems().size() < realPosition) {
                return;
            }
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
                        (DataBindingGridLayoutItem.Callback) (v, o, i) -> {
                            DataBindingGridLayoutItem.Callback callback = gridItem.getCallback();
                            if (callback != null) {
                                callback.onItemCallback(v, o, i);
                                return;
                            }
                            for (View view : views) {
                                if (view instanceof DataBindingGridLayoutItem) {
                                    DataBindingGridLayoutItem item =
                                            (DataBindingGridLayoutItem) view;
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
                if (view instanceof DataBindingGridLayoutItem) {
                    DataBindingGridLayoutItem gridItem = (DataBindingGridLayoutItem) view;
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
                if (view instanceof DataBindingGridLayoutItem) {
                    DataBindingGridLayoutItem gridItem = (DataBindingGridLayoutItem) view;
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
            if (view instanceof DataBindingGridLayoutItem) {
                DataBindingGridLayoutItem gridItem = (DataBindingGridLayoutItem) view;
                List<?> items = gridItem.getItems();
                return items.get(realPosition).hashCode();
            } else {
                return view.hashCode();
            }
        }

        public int getRealPosition(int position) {
            int currentPosition = 0;
            for (int type = 0; type < views.size(); type++) {
                View view = views.get(type);
                int itemCount;
                if (view instanceof DataBindingGridLayoutItem) {
                    DataBindingGridLayoutItem gridItem = (DataBindingGridLayoutItem) view;
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
            spanCounts.put(view, ((LayoutParams) view.getLayoutParams()).getSpanCount());
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
                                  @NonNull DataBindingGridLayoutItem vh) {
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
            FrameLayout newParent = new FrameLayout(context);
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

    /**
     * 하위 레이아웃 속성 클래스
     */
    public static class LayoutParams extends GridLayoutManager.LayoutParams {
        private int spanCount;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            resolveAttributes(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        private void resolveAttributes(@NonNull Context c, @Nullable AttributeSet attrs) {
            if (attrs == null) {
                spanCount = 1;
            } else {
                TypedArray a = c.obtainStyledAttributes(
                        attrs, R.styleable.DataBindingGridLayout_Layout);
                spanCount = a.getInt(
                        R.styleable.DataBindingGridLayout_Layout_spanCount,
                        1);
                a.recycle();
            }
        }

        public int getSpanCount() {
            return spanCount;
        }
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
}

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.nextstory.R;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 데이터바인딩용 {@link RecyclerView}
 *
 * @author troy
 * @version 1.0.4
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingRecyclerView extends RecyclerView {
    private final List<OnViewHolderCallback> onViewHolderCallbacks = new ArrayList<>();
    private int orientation;
    private DataBindingAdapter adapter;

    @BindingAdapter("bindItems")
    public static void bindItems(
            @NonNull final DataBindingRecyclerViewHolder viewHolderItem,
            @Nullable final List<?> items
    ) {
        Objects.requireNonNull(viewHolderItem);
        Objects.requireNonNull(items);
        viewHolderItem.setItems(items);
    }

    @BindingAdapter("onViewHolderCallback")
    public static void addOnViewHolderCallback(
            @NonNull final DataBindingRecyclerViewHolder viewHolderItem,
            @NonNull final OnViewHolderCallback callback
    ) {
        Objects.requireNonNull(viewHolderItem);
        Objects.requireNonNull(callback);
        viewHolderItem.setCallback(callback);
    }

    @BindingAdapter("onBottomScrollReached")
    public static void addOnBottomScrollReachedListener(
            @NonNull final DataBindingRecyclerView view,
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

    public DataBindingRecyclerView(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public DataBindingRecyclerView(@NonNull Context context,
                                   @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public DataBindingRecyclerView(@NonNull Context context,
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
                    context.obtainStyledAttributes(attrs, R.styleable.DataBindingRecyclerView);
            orientation = a.getInt(
                    R.styleable.DataBindingRecyclerView_android_orientation,
                    -1);
            if (orientation == -1) {
                orientation = VERTICAL;
            }
            spanCount = a.getInt(
                    R.styleable.DataBindingRecyclerView_spanCount,
                    1);
            reverseLayout = a.getBoolean(
                    R.styleable.DataBindingRecyclerView_reverseLayout,
                    false);
            a.recycle();
        }
        GridLayoutManager layoutManager = new PreventExceptionGridLayoutManager(
                context, spanCount, orientation, reverseLayout);
        super.setLayoutManager(layoutManager);
        super.setHasFixedSize(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter == null) {
            adapter = new DataBindingAdapter(onViewHolderCallbacks);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof DataBindingRecyclerViewHolder) {
                    DataBindingRecyclerViewHolder h = (DataBindingRecyclerViewHolder) child;
                    h.setParent(this);
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

    // endregion

    @Override
    public final void setHasFixedSize(boolean hasFixedSize) {
        // no-op
    }

    @Override
    public final boolean hasFixedSize() {
        return false;
    }

    public void addOnViewHolderCallback(@Nullable OnViewHolderCallback callback) {
        onViewHolderCallbacks.add(callback);
    }

    public void removeOnViewHolderCallback(@Nullable OnViewHolderCallback callback) {
        onViewHolderCallbacks.remove(callback);
    }

    public void clearOnViewHolderCallback() {
        onViewHolderCallbacks.clear();
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
     * 뷰 홀더 콜백
     */
    public interface OnViewHolderCallback {
        /**
         * 콜백 시 호출
         *
         * @param view     참조 뷰
         * @param item     항목
         * @param position 항목의 인덱스
         */
        void onViewHolderCallback(View view, Object item, int position);
    }

    /**
     * 항목 변경 사항 체크용 어노테이션
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DiffKey {
    }

    /**
     * 내부 항목 비교 콜백
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class InternalDiffUtilCallback extends DiffUtil.Callback {
        private final List<?> oldList;
        private final List<?> newList;

        private InternalDiffUtilCallback(List<?> oldList, List<?> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldList.get(oldItemPosition);
            Object newItem = newList.get(newItemPosition);
            Map<String, Object> olds = getAnnotatedFields(oldItem, DiffKey.class);
            Map<String, Object> news = getAnnotatedFields(newItem, DiffKey.class);
            if (olds.keySet().size() > 0 && news.keySet().size() > 0) {
                boolean isEqual = true;
                for (String key : olds.keySet()) {
                    Object oldField = olds.get(key);
                    Object newField = news.get(key);
                    isEqual &= oldField.equals(newField);
                }
                return isEqual;
            } else {
                return oldItem.equals(newItem);
            }
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }

        private Map<String, Object> getAnnotatedFields(Object item,
                                                       Class<? extends Annotation> annotation) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Field field : item.getClass().getDeclaredFields()) {
                if (field.getAnnotation(annotation) != null) {
                    try {
                        boolean isAccessible = field.isAccessible();
                        field.setAccessible(true);
                        result.put(field.getName(), field.get(item));
                        field.setAccessible(isAccessible);
                    } catch (IllegalAccessException ignore) {
                        // no-op
                    }
                }
            }
            return result;
        }
    }

    /**
     * 어댑터
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class DataBindingAdapter extends Adapter<DataBindingAdapter.DataBindingViewHolder> {
        private final List<ViewHolderType> types = new ArrayList<>();
        private final List<OnViewHolderCallback> onViewHolderCallbacks;

        private DataBindingAdapter(List<OnViewHolderCallback> onViewHolderCallbacks) {
            this.onViewHolderCallbacks = onViewHolderCallbacks;
            setHasStableIds(true);
        }

        /**
         * 바인딩 id를 반환
         *
         * @param id 아이디
         * @return id, 찾을 수 없으면 {@code -1}을 반환함
         */
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

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (types.size() == 0) {
                            return 1;
                        }
                        return types.get(getItemViewType(position)).getSpanCount();
                    }
                });
            }
        }

        @NonNull
        @Override
        public DataBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolderType type = types.get(viewType);
            if (type.originalView instanceof DataBindingRecyclerViewHolder) {
                if (parent.isInEditMode()) {
                    return new DataBindingViewHolder(type.originalView);
                }
                try {
                    DataBindingRecyclerViewHolder v =
                            (DataBindingRecyclerViewHolder) type.originalView;
                    ViewDataBinding binding = DataBindingUtil.inflate(
                            LayoutInflater.from(parent.getContext()),
                            v.getLayoutRes(),
                            null,
                            false);
                    binding.getRoot().setVisibility(View.GONE);
                    return new DataBindingViewHolder(binding, v);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    throw new IllegalStateException("inflate error.");
                }
            } else {
                ViewParent viewParent = type.originalView.getParent();
                if (viewParent != null) {
                    if (viewParent instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) viewParent;
                        viewGroup.removeView(type.originalView);
                    }
                }
                return new DataBindingViewHolder(type.originalView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
            if (holder.itemView.isInEditMode()) {
                return;
            }
            if (holder.binding == null) {
                return;
            }
            int viewType = getItemViewType(position);
            int realPosition = getRealPosition(position);
            if (!types.get(viewType).isViewHolderType()) {
                return;
            }
            if (types.get(viewType).getItems().size() < realPosition) {
                return;
            }
            holder.binding.getRoot().post(() ->
                    holder.binding.getRoot().setVisibility(View.VISIBLE));
            if (holder.getItemVarId() != -1) {
                holder.binding.setVariable(holder.getItemVarId(),
                        types.get(viewType).getItems().get(realPosition));
            }
            if (holder.getPositionVarId() != -1) {
                holder.binding.setVariable(holder.getPositionVarId(), realPosition);
            }
            if (holder.getCallbackVarId() != -1) {
                holder.binding.setVariable(holder.getCallbackVarId(),
                        (OnViewHolderCallback) (v, o, i) -> {
                            OnViewHolderCallback callback = types.get(viewType).getCallback();
                            if (callback != null) {
                                callback.onViewHolderCallback(v, o, i);
                                return;
                            }
                            for (OnViewHolderCallback c : onViewHolderCallbacks) {
                                if (c != null) {
                                    c.onViewHolderCallback(v, o, i);
                                }
                            }
                        });
            }
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            for (ViewHolderType type : types) {
                List<?> items = type.getItems();
                itemCount += items.size();
            }
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            int currentPosition = 0;
            for (int type = 0; type < types.size(); type++) {
                ViewHolderType item = types.get(type);
                for (int i = 0; i < item.getItems().size(); i++) {
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
            List<?> list = types.get(viewType).getItems();
            return list.get(realPosition).hashCode();
        }

        public int getRealPosition(int position) {
            int currentPosition = 0;
            for (int i = 0; i < types.size(); i++) {
                ViewHolderType type = types.get(i);
                for (int j = 0; j < type.getItems().size(); j++) {
                    if (currentPosition == position) {
                        return j;
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
            ViewHolderType newType = new ViewHolderType(view);
            types.add(newType);
            if (!view.isInEditMode() && view instanceof DataBindingRecyclerViewHolder) {
                DataBindingRecyclerViewHolder holder = (DataBindingRecyclerViewHolder) view;
                onViewHolderCallbacks.add(holder.getCallback());
                bind(view, holder.getItems());
            }
        }

        /**
         * 데이터 바인딩
         *
         * @param v       바인딩할 뷰
         * @param newList 모델 (목록)
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        void bind(View v, List<?> newList) {
            int startPosition = 0;
            for (ViewHolderType type : types) {
                if (type.originalView == v) {
                    List<?> oldList = type.getOldItems();
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                            new InternalDiffUtilCallback(oldList, newList));
                    int finalStartPosition = startPosition;
                    diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(finalStartPosition + position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(finalStartPosition + position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(
                                    finalStartPosition + fromPosition,
                                    finalStartPosition + toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count, Object payload) {
                            notifyItemRangeChanged(finalStartPosition + position, count, payload);
                        }
                    });
                    break;
                } else {
                    startPosition += type.getItems().size();
                }
            }
        }

        /**
         * 뷰 홀더 타입
         */
        private static class ViewHolderType {
            private final View originalView;
            private final int spanCount;
            @NonNull
            private final List<?> items = Collections.singletonList(new Object());

            public ViewHolderType(View originalView) {
                this.originalView = originalView;
                LayoutParams params = (LayoutParams) originalView.getLayoutParams();
                this.spanCount = params.getSpanCount();
            }

            public boolean isViewHolderType() {
                return originalView instanceof DataBindingRecyclerViewHolder;
            }

            @NonNull
            public List<?> getItems() {
                if (originalView instanceof DataBindingRecyclerViewHolder) {
                    DataBindingRecyclerViewHolder h =
                            (DataBindingRecyclerViewHolder) originalView;
                    if (h.isInEditMode()) {
                        return Collections.singletonList(new Object());
                    }
                    return h.getItems();
                }
                return items;
            }

            @NonNull
            public List<?> getOldItems() {
                if (originalView instanceof DataBindingRecyclerViewHolder) {
                    DataBindingRecyclerViewHolder h =
                            (DataBindingRecyclerViewHolder) originalView;
                    if (h.isInEditMode()) {
                        return Collections.singletonList(new Object());
                    }
                    return h.getOldItems();
                }
                return items;
            }

            @Nullable
            public OnViewHolderCallback getCallback() {
                if (originalView instanceof DataBindingRecyclerViewHolder) {
                    DataBindingRecyclerViewHolder h =
                            (DataBindingRecyclerViewHolder) originalView;
                    return h.getCallback();
                }
                return null;
            }

            public int getSpanCount() {
                return spanCount;
            }
        }

        /**
         * 뷰 홀더
         */
        private static class DataBindingViewHolder extends ViewHolder {
            private final ViewDataBinding binding;
            private int itemVarId = -1;
            private int positionVarId = -1;
            private int callbackVarId = -1;

            public DataBindingViewHolder(@NonNull ViewDataBinding binding,
                                         @NonNull DataBindingRecyclerViewHolder vh) {
                super(createView(binding.getRoot(),
                        (MarginLayoutParams) vh.getLayoutParams()));
                this.binding = binding;
                String packageName = vh.getContext().getPackageName();
                itemVarId = getBindingId(
                        packageName.concat(".BR.").concat(vh.getItemBindingName()));
                positionVarId = getBindingId(
                        packageName.concat(".BR.").concat(vh.getPositionBindingName()));
                callbackVarId = getBindingId(
                        packageName.concat(".BR.").concat(vh.getCallbackBindingName()));
            }

            public DataBindingViewHolder(@NonNull View itemView) {
                super(createView(itemView, null));
                this.binding = null;
            }

            private static View createView(View v, ViewGroup.MarginLayoutParams originalParams) {
                Context context = v.getContext();
                ViewGroup.MarginLayoutParams params = originalParams == null
                        ? (ViewGroup.MarginLayoutParams) v.getLayoutParams()
                        : originalParams;
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
    }

    /**
     * 예외 방지용 {@link GridLayoutManager}
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class PreventExceptionGridLayoutManager extends GridLayoutManager {
        private final AtomicBoolean canScroll = new AtomicBoolean(false);

        public PreventExceptionGridLayoutManager(Context context,
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

        @Override
        public boolean canScrollHorizontally() {
            return canScroll.get() && super.canScrollHorizontally();
        }

        @Override
        public boolean canScrollVertically() {
            return canScroll.get() && super.canScrollVertically();
        }

        @Override
        public void onItemsAdded(RecyclerView recyclerView,
                                 int positionStart,
                                 int itemCount) {
            super.onItemsAdded(recyclerView, positionStart, itemCount);
            canScroll.set(false);
            postOnAnimation(() -> postOnAnimation(() -> canScroll.set(true)));
        }

        @Override
        public void onItemsChanged(RecyclerView recyclerView) {
            super.onItemsChanged(recyclerView);
            canScroll.set(false);
            postOnAnimation(() -> postOnAnimation(() -> canScroll.set(true)));
        }

        @Override
        public void onItemsRemoved(RecyclerView recyclerView,
                                   int positionStart,
                                   int itemCount) {
            super.onItemsRemoved(recyclerView, positionStart, itemCount);
            canScroll.set(false);
            postOnAnimation(() -> postOnAnimation(() -> canScroll.set(true)));
        }

        @Override
        public void onItemsUpdated(RecyclerView recyclerView,
                                   int positionStart,
                                   int itemCount,
                                   Object payload) {
            super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
            canScroll.set(false);
            postOnAnimation(() -> postOnAnimation(() -> canScroll.set(true)));
        }

        @Override
        public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
            super.onItemsMoved(recyclerView, from, to, itemCount);
            canScroll.set(false);
            postOnAnimation(() -> postOnAnimation(() -> canScroll.set(true)));
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
                        attrs, R.styleable.DataBindingRecyclerView_Layout);
                spanCount = a.getInt(
                        R.styleable.DataBindingRecyclerView_Layout_spanCount,
                        1);
                a.recycle();
            }
        }

        public int getSpanCount() {
            return spanCount;
        }
    }
}

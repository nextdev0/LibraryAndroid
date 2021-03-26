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

import com.nextstory.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link DataBindingRecyclerView} 뷰 홀더
 *
 * @author troy
 * @version 1.0.1
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingRecyclerViewHolder extends FrameLayout {
    private final int layoutRes;
    private List<Object> items = new ArrayList<>();
    private List<Object> oldItems = new ArrayList<>();
    private WeakReference<DataBindingRecyclerView> parent;
    private DataBindingRecyclerView.OnViewHolderCallback callback = null;
    private final String itemBindingName, positionBindingName, callbackBindingName;

    public DataBindingRecyclerViewHolder(Context context) {
        this(context, null);
    }

    public DataBindingRecyclerViewHolder(Context context,
                                         @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataBindingRecyclerViewHolder(Context context,
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
                    attrs, R.styleable.DataBindingRecyclerViewHolder);
            layoutRes = a.getResourceId(
                    R.styleable.DataBindingRecyclerViewHolder_layout,
                    0);
            if (a.hasValue(R.styleable.DataBindingRecyclerViewHolder_itemBindingName)) {
                itemBindingName = a.getString(
                        R.styleable.DataBindingRecyclerViewHolder_itemBindingName);
            } else {
                itemBindingName = "item";
            }
            if (a.hasValue(R.styleable.DataBindingRecyclerViewHolder_positionBindingName)) {
                positionBindingName = a.getString(
                        R.styleable.DataBindingRecyclerViewHolder_positionBindingName);
            } else {
                positionBindingName = "position";
            }
            if (a.hasValue(R.styleable.DataBindingRecyclerViewHolder_callbackBindingName)) {
                callbackBindingName = a.getString(
                        R.styleable.DataBindingRecyclerViewHolder_callbackBindingName);
            } else {
                callbackBindingName = "callback";
            }
            a.recycle();
        }
        if (isInEditMode() && layoutRes != 0 && getChildCount() == 0) {
            View newChild = View.inflate(getContext(), layoutRes, null);
            addView(newChild, new LayoutParams(-1, -1));
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
    @NonNull
    List<?> getItems() {
        return items;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    List<?> getOldItems() {
        return oldItems;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setItems(@NonNull List<?> items) {
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items = new ArrayList<>();
        this.items.addAll(items);
        if (parent != null && parent.get() != null) {
            DataBindingRecyclerView.DataBindingAdapter adapter =
                    (DataBindingRecyclerView.DataBindingAdapter) parent.get().getAdapter();
            if (adapter != null) {
                adapter.bind(this, items);
            }
        } else {
            postDelayed(() -> {
                if (parent != null && parent.get() != null) {
                    DataBindingRecyclerView.DataBindingAdapter adapter =
                            (DataBindingRecyclerView.DataBindingAdapter) parent.get().getAdapter();
                    if (adapter != null) {
                        adapter.bind(this, items);
                    }
                }
            }, 50L);
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public String getItemBindingName() {
        return itemBindingName;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public String getPositionBindingName() {
        return positionBindingName;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public String getCallbackBindingName() {
        return callbackBindingName;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setParent(DataBindingRecyclerView parent) {
        this.parent = new WeakReference<>(parent);
    }

    @Nullable
    public DataBindingRecyclerView.OnViewHolderCallback getCallback() {
        return callback;
    }

    void setCallback(@NonNull DataBindingRecyclerView.OnViewHolderCallback callback) {
        this.callback = callback;
    }
}

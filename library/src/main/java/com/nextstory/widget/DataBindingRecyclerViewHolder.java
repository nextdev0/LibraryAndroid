package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nextstory.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link DataBindingRecyclerView} 뷰 홀더
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class DataBindingRecyclerViewHolder extends FrameLayout {
    private final int layoutRes;
    private List<?> items = new ArrayList<>();
    private WeakReference<DataBindingRecyclerView> parent;
    private DataBindingRecyclerView.OnViewHolderCallback callback = null;

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
        } else {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.DataBindingRecyclerViewHolder);
            layoutRes = a.getResourceId(R.styleable.DataBindingRecyclerViewHolder_layout, 0);
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

    @NonNull
    public List<?> getItems() {
        return items;
    }

    void setItems(@NonNull List<?> items) {
        this.items = items;
        if (parent != null && parent.get() != null) {
            RecyclerView.Adapter<?> adapter = parent.get().getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            postDelayed(() -> {
                if (parent != null && parent.get() != null) {
                    RecyclerView.Adapter<?> adapter = parent.get().getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }, 50L);
        }
    }

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

package com.nextstory.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nextstory.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 데이터바인딩 뷰 페이저
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
public final class DataBindingViewPager extends ViewPager {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final boolean isInfiniteScrollEnabled;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private PagerAdapter adapter;

    public DataBindingViewPager(@NonNull Context context) {
        this(context, null);
    }

    public DataBindingViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (attrs == null) {
            isInfiniteScrollEnabled = false;
        } else {
            TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.DataBindingViewPager);
            isInfiniteScrollEnabled = a.getBoolean(
                    R.styleable.DataBindingViewPager_infiniteScrollEnabled,
                    false);
            a.recycle();
        }
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter == null) {
            InternalAdapter internalAdapter = new InternalAdapter();
            adapter = isInfiniteScrollEnabled
                    ? new InfinitePagerAdapterWrapper(internalAdapter)
                    : internalAdapter;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof DataBindingViewPagerItem) {
                    ((DataBindingViewPagerItem) child).setParent(this);
                }
                internalAdapter.add(child);
            }
            removeAllViews();
            super.setAdapter(adapter);
        }
    }

    @Nullable
    @Override
    public final PagerAdapter getAdapter() {
        return super.getAdapter();
    }

    @Override
    public final void setAdapter(@Nullable PagerAdapter adapter) {
        // no-op
    }

    /**
     * 내부 어댑터 wrapper class
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class InternalAdapter extends PagerAdapter {
        private final List<View> views = new ArrayList<>();

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            int itemCount = 0;
            for (View view : views) {
                if (view instanceof DataBindingViewPagerItem) {
                    DataBindingViewPagerItem gridItem = (DataBindingViewPagerItem) view;
                    List<?> items = gridItem.getItems();
                    itemCount += items.size();
                } else {
                    itemCount++;
                }
            }
            return itemCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int viewType = getItemViewType(position);
            View view = views.get(viewType);
            if (view instanceof DataBindingViewPagerItem) {
                DataBindingViewPagerItem v = (DataBindingViewPagerItem) view;
                ViewDataBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(container.getContext()),
                        v.getLayoutRes(),
                        null,
                        false);
                int realPosition = getRealPosition(position);
                String packageName = container.getContext().getPackageName();
                int itemVarId = getBindingId(
                        packageName.concat(".BR.").concat(v.getItemBindingName()));
                int positionVarId = getBindingId(
                        packageName.concat(".BR.").concat(v.getPositionBindingName()));
                int callbackVarId = getBindingId(
                        packageName.concat(".BR.").concat(v.getCallbackBindingName()));
                if (!container.isInEditMode()) {
                    if (itemVarId != -1) {
                        binding.setVariable(
                                itemVarId,
                                v.getItems().get(realPosition));
                    }
                    if (positionVarId != -1) {
                        binding.setVariable(
                                positionVarId,
                                realPosition);
                    }
                    if (callbackVarId != -1) {
                        binding.setVariable(
                                callbackVarId,
                                (DataBindingViewPagerItem.Callback) (v1, o, i) -> {
                                    DataBindingViewPagerItem.Callback callback = v.getCallback();
                                    if (callback != null) {
                                        callback.onItemCallback(v1, o, i);
                                        return;
                                    }
                                    for (View v2 : views) {
                                        if (v2 instanceof DataBindingViewPagerItem) {
                                            DataBindingViewPagerItem item =
                                                    (DataBindingViewPagerItem) v2;
                                            if (item.getCallback() != null) {
                                                item.getCallback().onItemCallback(v1, o, i);
                                            }
                                        }
                                    }
                                });
                    }
                }
                container.addView(binding.getRoot());
                return binding.getRoot();
            } else {
                ViewParent viewParent = view.getParent();
                if (viewParent != null) {
                    if (viewParent instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) viewParent;
                        viewGroup.removeView(view);
                    }
                }
                DataBindingViewPagerViewItem newView =
                        new DataBindingViewPagerViewItem(container.getContext());
                newView.addView(view);
                container.addView(newView);
                return newView;
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container,
                                int position,
                                @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        /**
         * 어댑터상 위치에서 뷰 타입을 찾아 반환함
         *
         * @param position 어댑터상 위치
         * @return 뷰 타입
         */
        public int getItemViewType(int position) {
            int currentPosition = 0;
            for (int type = 0; type < views.size(); type++) {
                View view = views.get(type);
                int itemCount;
                if (view instanceof DataBindingViewPagerItem) {
                    DataBindingViewPagerItem gridItem = (DataBindingViewPagerItem) view;
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
                if (view instanceof DataBindingViewPagerItem) {
                    DataBindingViewPagerItem gridItem = (DataBindingViewPagerItem) view;
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
         * 뷰 추가
         *
         * @param view 뷰
         */
        public void add(View view) {
            views.add(view);
        }
    }

    /**
     * 무한 스크롤 어댑터 wrapper class
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static class InfinitePagerAdapterWrapper extends PagerAdapter {
        private final PagerAdapter adapter;

        public InfinitePagerAdapterWrapper(PagerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getCount() {
            if (getRealCount() == 0) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }

        /**
         * 실제 항목 수를 가져옴
         *
         * @return 항목 수
         */
        public int getRealCount() {
            return adapter.getCount();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int virtualPosition = getRealCount() == 0 ? 0 : position % getRealCount();
            return adapter.instantiateItem(container, virtualPosition);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object o) {
            int virtualPosition = getRealCount() == 0 ? 0 : position % getRealCount();
            adapter.destroyItem(container, virtualPosition, o);
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            adapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            adapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void startUpdate(@NonNull ViewGroup container) {
            adapter.startUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int virtualPosition = position % getRealCount();
            return adapter.getPageTitle(virtualPosition);
        }

        @Override
        public float getPageWidth(int position) {
            return adapter.getPageWidth(position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object o) {
            adapter.setPrimaryItem(container, position, o);
        }

        @Override
        public void unregisterDataSetObserver(@NonNull DataSetObserver observer) {
            adapter.unregisterDataSetObserver(observer);
        }

        @Override
        public void registerDataSetObserver(@NonNull DataSetObserver observer) {
            adapter.registerDataSetObserver(observer);
        }

        @Override
        public void notifyDataSetChanged() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return adapter.getItemPosition(object);
        }
    }
}

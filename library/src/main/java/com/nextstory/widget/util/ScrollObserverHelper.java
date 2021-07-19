package com.nextstory.widget.util;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author troy
 * @since 2.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ScrollObserverHelper {
    private final ScrollObservable scrollObservable;

    public ScrollObserverHelper(ScrollObservable scrollObservable) {
        this.scrollObservable = scrollObservable;
    }

    public void onAttachedToWindow() {
        View contentView = scrollObservable.getContentView();

        if (contentView instanceof RecyclerView) {
            ((RecyclerView) contentView).addOnScrollListener(
                    new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView,
                                               int dx, int dy) {
                            int scrollY = Math.abs(recyclerView.computeVerticalScrollOffset());
                            notifyScrollChanged(scrollY);
                        }
                    });
            return;
        }

        if (contentView instanceof NestedScrollView) {
            NestedScrollView nestedScrollView = (NestedScrollView) contentView;
            NestedScrollViewListeners.addListener(nestedScrollView, (v,
                                                                     scrollX, scrollY,
                                                                     oldScrollX, oldScrollY) -> {
                int y = Math.abs(v.getScrollY());
                notifyScrollChanged(y);
            });
            return;
        }

        contentView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (!contentView.getViewTreeObserver().isAlive()) {
                            contentView.getViewTreeObserver()
                                    .removeOnScrollChangedListener(this);
                            return;
                        }
                        if (contentView instanceof AbsListView) {
                            AbsListView absListView = (AbsListView) contentView;
                            if (absListView.getChildCount() > 0) {
                                View firstVisibleChild = absListView.getChildAt(0);
                                if (firstVisibleChild != null) {
                                    notifyScrollChanged(Math.abs(firstVisibleChild.getTop()
                                            + absListView.getFirstVisiblePosition()
                                            * firstVisibleChild.getHeight()));
                                    return;
                                }
                                notifyScrollChanged(0);
                            }
                            return;
                        }
                        int y = Math.abs(contentView.getScrollY());
                        notifyScrollChanged(y);
                    }
                });
    }

    private void notifyScrollChanged(int scrollY) {
        scrollObservable.onScrollChanged(scrollY);
    }

    /**
     * 구현 인터페이스
     */
    public interface ScrollObservable {
        /**
         * @return 스크롤 가능한 컨텐츠 뷰
         */
        View getContentView();

        /**
         * 스크롤 변경 시 호출
         *
         * @param scrollY 수직 스크롤 위치
         */
        void onScrollChanged(int scrollY);
    }

    /**
     * {@link NestedScrollView.OnScrollChangeListener} 여러개 지정이 가능한 리스너
     */
    static class NestedScrollViewListeners implements NestedScrollView.OnScrollChangeListener {
        static Field field = null;
        final Set<NestedScrollView.OnScrollChangeListener> nestedScrollViewListeners =
                new HashSet<>();

        public static void addListener(NestedScrollView v,
                                       NestedScrollView.OnScrollChangeListener l) {
            if (field == null) {
                try {
                    field = v.getClass().getDeclaredField("mOnScrollChangeListener");
                    field.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            try {
                NestedScrollView.OnScrollChangeListener currentListener =
                        (NestedScrollView.OnScrollChangeListener) field.get(v);
                if (!(currentListener instanceof NestedScrollViewListeners)) {
                    NestedScrollViewListeners newListeners = new NestedScrollViewListeners();
                    newListeners.nestedScrollViewListeners.add(currentListener);
                    newListeners.nestedScrollViewListeners.add(l);
                    v.setOnScrollChangeListener(newListeners);
                } else {
                    NestedScrollViewListeners newListeners =
                            (NestedScrollViewListeners) currentListener;
                    newListeners.nestedScrollViewListeners.add(l);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onScrollChange(NestedScrollView v,
                                   int scrollX, int scrollY,
                                   int oldScrollX, int oldScrollY) {
            for (NestedScrollView.OnScrollChangeListener l : nestedScrollViewListeners) {
                if (l != null) {
                    l.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);
                }
            }
        }
    }
}

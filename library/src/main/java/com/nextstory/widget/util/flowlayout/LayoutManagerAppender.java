package com.nextstory.widget.util.flowlayout;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Dudakov (2016.4.10)
 * @see <a href="https://github.com/simonebortolin/FlowLayoutManager">원본 레포지토리</a>
 * @since 1.2
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("UnusedDeclaration")
public class LayoutManagerAppender {
    View mView;
    RecyclerView.LayoutManager mLayoutManager;
    Rect mRect;
    Alignment alignment;

    public LayoutManagerAppender(View view,
                                 RecyclerView.LayoutManager layoutManager,
                                 Rect rect,
                                 Alignment alignment) {
        mView = view;
        mLayoutManager = layoutManager;
        this.mRect = new Rect(rect);
        this.alignment = alignment;
    }

    public void layout(int addition) {
        if (alignment == Alignment.CENTER) {
            mLayoutManager.layoutDecorated(
                    mView,
                    mRect.left + addition,
                    mRect.top,
                    mRect.right + addition,
                    mRect.bottom);
        } else {
            mLayoutManager.layoutDecorated(
                    mView,
                    mRect.left,
                    mRect.top,
                    mRect.right,
                    mRect.bottom);
        }
    }
}

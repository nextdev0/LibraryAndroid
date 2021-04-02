package com.nextstory.widget;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * {@link DataBindingViewPager} 일반뷰 항목
 *
 * @author troy
 * @since 1.1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class DataBindingViewPagerViewItem extends FrameLayout {
    public DataBindingViewPagerViewItem(@NonNull Context context) {
        super(context);
    }
}

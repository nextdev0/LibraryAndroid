package com.nextstory.widget;

import android.view.View;

/**
 * 항목 콜백 인터페이스
 *
 * @author troy
 * @since 1.1
 */
public interface DataBindingItemCallback {
    /**
     * 콜백 시 호출
     *
     * @param view     참조 뷰
     * @param item     항목
     * @param position 항목의 인덱스
     */
    void onItemCallback(View view, Object item, int position);
}

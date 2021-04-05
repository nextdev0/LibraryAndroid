package com.nextstory.widget;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

/**
 * 바인딩 어댑터
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class SimpleRatingBarBindingAdapters {
    private SimpleRatingBarBindingAdapters() {
        // no-op
    }

    /**
     * 레이팅 값 지정
     *
     * @param v     뷰
     * @param value 레이팅값
     * @see BaseRatingBar
     */
    @BindingAdapter("srb_rating")
    public static void setRating(@NonNull BaseRatingBar v, float value) {
        v.setRating(value);
    }

    /**
     * 레이팅값 반환
     *
     * @param v 뷰
     * @see BaseRatingBar
     */
    @InverseBindingAdapter(attribute = "srb_rating", event = "srb_ratingAttrChanged")
    public static float getRating(@NonNull BaseRatingBar v) {
        return v.getRating();
    }

    /**
     * 레이팅값 변경 리스너 설정
     *
     * @param v                 뷰
     * @param ratingAttrChanged inverse 어댑터, 값 변경 반환용으로 사용
     * @see BaseRatingBar
     */
    @BindingAdapter("srb_ratingAttrChanged")
    public static void setOnRatingChangeListener(@NonNull BaseRatingBar v,
                                                 InverseBindingListener ratingAttrChanged) {
        if (ratingAttrChanged != null) {
            v.setOnRatingChangeListener((ratingBar, rating, fromUser) ->
                    ratingAttrChanged.onChange());
        }
    }
}

package com.nextstory.widget.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.RestrictTo;

/**
 * SimpleRatingBar 위젯 상태 저장용 클래스
 *
 * @author willy (2017.11.16)
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SimpleRatingBarSavedState extends View.BaseSavedState {
    public static final Creator<SimpleRatingBarSavedState> CREATOR
            = new Creator<SimpleRatingBarSavedState>() {
        public SimpleRatingBarSavedState createFromParcel(Parcel in) {
            return new SimpleRatingBarSavedState(in);
        }

        public SimpleRatingBarSavedState[] newArray(int size) {
            return new SimpleRatingBarSavedState[size];
        }
    };
    private float rating;

    public SimpleRatingBarSavedState(Parcelable superState) {
        super(superState);
    }

    private SimpleRatingBarSavedState(Parcel in) {
        super(in);
        rating = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeFloat(rating);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

package com.nextstory.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * 위젯 상태 저장용 클래스
 *
 * @author willy (2017.11.16)
 * @see <a href="https://github.com/williamyyu/SimpleRatingBar">원본 레포지토리</a>
 * @since 1.0
 */
class SavedState extends View.BaseSavedState {
    public static final Creator<SavedState> CREATOR
            = new Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
    private float rating;

    /**
     * Constructor called from {@link BaseRatingBar#onSaveInstanceState()}
     */
    SavedState(Parcelable superState) {
        super(superState);
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    private SavedState(Parcel in) {
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

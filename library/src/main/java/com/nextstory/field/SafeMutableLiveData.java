package com.nextstory.field;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link MutableLiveData}에서 상태 유지가 가능한 버전
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public class SafeMutableLiveData<T> extends MutableLiveData<T> implements SaveInstanceStateField {
    private String key = "";

    public SafeMutableLiveData() {
        super();
    }

    /**
     * @param defaultValue 초기값
     * @deprecated {@link NonNullLiveData}로 대신 사용할 것
     */
    @Deprecated
    public SafeMutableLiveData(T defaultValue) {
        super();
        setValue(defaultValue);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Object object = savedInstanceState.get(getKey());
        if (object != null) {
            super.setValue((T) object);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        T value = super.getValue();
        if (value != null) {
            if (value instanceof Parcelable) {
                outState.putParcelable(getKey(), (Parcelable) value);
            } else {
                outState.putSerializable(getKey(), (Serializable) value);
            }
        }
    }

    @NonNull
    public T requireValue() {
        return Objects.requireNonNull(getValue());
    }
}

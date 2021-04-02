package com.nextstory.field;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@code null}을 가질 수 없는 LiveData
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class NonNullLiveData<T> extends MutableLiveData<T> implements SaveInstanceStateField {
    private final boolean isSaveInstanceStateEnabled;
    private String key = "";

    public NonNullLiveData(@NonNull T defaultValue) {
        this(Objects.requireNonNull(defaultValue), false);
    }

    public NonNullLiveData(@NonNull T defaultValue, boolean isSaveInstanceStateEnabled) {
        super();
        this.isSaveInstanceStateEnabled = isSaveInstanceStateEnabled;
        setValue(Objects.requireNonNull(defaultValue));
    }

    @NonNull
    @Override
    public T getValue() {
        return Objects.requireNonNull(super.getValue());
    }

    @Override
    public void setValue(@NonNull T value) {
        super.setValue(Objects.requireNonNull(value));
    }

    @Override
    public void postValue(@NonNull T value) {
        super.postValue(Objects.requireNonNull(value));
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
        if (value != null && isSaveInstanceStateEnabled) {
            if (value instanceof Parcelable) {
                outState.putParcelable(getKey(), (Parcelable) value);
            } else {
                outState.putSerializable(getKey(), (Serializable) value);
            }
        }
    }
}

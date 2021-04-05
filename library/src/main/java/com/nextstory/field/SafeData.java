package com.nextstory.field;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * 상태 유지가 가능한 데이터
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class SafeData<T> implements SaveInstanceStateField {
    private String key = "";
    private T value;

    public SafeData() {
        value = null;
    }

    public SafeData(T defaultValue) {
        value = defaultValue;
    }

    public T getValue() {
        return value;
    }

    public T requireValue() {
        return Objects.requireNonNull(value);
    }

    public void setValue(T value) {
        this.value = value;
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
            value = (T) object;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (value != null) {
            if (value instanceof Parcelable) {
                outState.putParcelable(getKey(), (Parcelable) value);
            } else {
                outState.putSerializable(getKey(), (Serializable) value);
            }
        }
    }
}

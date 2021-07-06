package com.nextstory.util;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * 데이터바인딩 유틸
 *
 * @author troy
 * @see DataBindingUtil
 * @since 2.0
 */
public final class Binding<T extends ViewDataBinding> {
    private final int res;
    private T binding;

    public Binding(@LayoutRes int res) {
        this.res = res;
    }

    public void setContentView(Activity activity) {
        binding = DataBindingUtil.setContentView(activity, res);
    }

    public void inflate(Fragment fragment) {
        inflate(fragment.getLayoutInflater());
    }

    public void inflate(Activity activity) {
        inflate(activity.getLayoutInflater());
    }

    public void inflate(LayoutInflater layoutInflater) {
        binding = DataBindingUtil.inflate(layoutInflater, res, null, false);
    }

    @NonNull
    public T get() {
        return Objects.requireNonNull(binding);
    }
}

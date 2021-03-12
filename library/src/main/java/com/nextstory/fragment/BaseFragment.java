package com.nextstory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * 데이터바인딩 적용 기본 프래그먼트
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseFragment<B extends ViewDataBinding> extends AbstractBaseFragment {
    private B binding = null;

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) getClass().getGenericSuperclass();
            if (parameterizedType != null) {
                try {
                    Method method = ((Class<?>) parameterizedType.getActualTypeArguments()[0])
                            .getMethod("inflate", LayoutInflater.class);
                    binding = (B) Objects.requireNonNull(method.invoke(null, getLayoutInflater()));
                } catch (NoSuchMethodException
                        | InvocationTargetException
                        | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    /**
     * @return 바인딩 인스턴스
     */
    protected B getBinding() {
        return binding;
    }
}

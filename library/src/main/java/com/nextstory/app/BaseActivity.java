package com.nextstory.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 데이터바인딩 적용 기본 액티비티
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseActivity<B extends ViewDataBinding> extends AbstractBaseActivity {
    private B binding = null;

    @CallSuper
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (binding == null) {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
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
        if (binding != null) {
            super.setContentView(binding.getRoot());
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(View view) {
        super.setContentView(view);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    /**
     * @return 바인딩 인스턴스
     */
    @CallSuper
    protected B getBinding() {
        return binding;
    }
}

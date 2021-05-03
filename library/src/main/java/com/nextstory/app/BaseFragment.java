package com.nextstory.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.nextstory.util.Unsafe;

import java.util.Objects;

/**
 * 데이터바인딩 적용 기본 프래그먼트
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseFragment<B extends ViewDataBinding> extends AbstractBaseFragment {
    private B binding = null;

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            Class<?> klass = Unsafe.getGenericClass(this, 0);
            if (klass != null) {
                binding = Unsafe.invoke(klass, "inflate", getLayoutInflater());
            }
        }
        return Objects.requireNonNull(binding).getRoot();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    /**
     * @return 바인딩 인스턴스
     */
    @CallSuper
    protected B getBinding() {
        return binding;
    }
}

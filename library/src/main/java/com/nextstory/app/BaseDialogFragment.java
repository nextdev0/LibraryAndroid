package com.nextstory.app;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nextstory.R;
import com.nextstory.util.Unsafe;

import java.lang.ref.WeakReference;

/**
 * 기본 다이얼로그 프래그먼트
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseDialogFragment<B extends ViewDataBinding> extends DialogFragment {
    private BaseDialog<B> dialog = null;
    private WeakReference<Bundle> savedInstanceState = null;

    @Override
    public int getTheme() {
        return R.style.Theme_Dialog_Base;
    }

    @CallSuper
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = new WeakReference<>(savedInstanceState);
        dialog = new BaseDialog<B>(requireContext(), getTheme()) {
        };
        return dialog;
    }

    @CallSuper
    @Override
    public void onStart() {
        if (dialog.binding == null) {
            Class<?> klass = Unsafe.getGenericClass(this, 0);
            if (klass != null) {
                dialog.binding = Unsafe.invoke(klass, "inflate", getLayoutInflater());
            }
        }
        super.onStart();
        onDialogCreated(dialog, savedInstanceState == null ? null : savedInstanceState.get());
    }

    @CallSuper
    public void onDialogCreated(BaseDialog<B> dialog, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dialog.onCreate(null);
        }
    }

    public final void show(@NonNull FragmentActivity activity) {
        show(activity.getSupportFragmentManager());
    }

    public final void show(@NonNull FragmentManager fragmentManager) {
        super.show(fragmentManager, getClass().getSimpleName());
    }

    public final void show(@NonNull Fragment fragment) {
        show(fragment.getParentFragmentManager());
    }

    public final void show(@NonNull FragmentTransaction fragmentTransaction) {
        super.show(fragmentTransaction, getClass().getSimpleName());
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    /**
     * 아무동작 없음
     */
    public final void nothing() {
        // no-op
    }

    /**
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected B getBinding() {
        return dialog.getBinding();
    }
}

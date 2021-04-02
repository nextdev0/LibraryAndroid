package com.nextstory.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
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
import com.nextstory.util.theme.ThemeHelpers;
import com.nextstory.util.theme.ThemeType;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * 기본 바텀시트 다이얼로그 프래그먼트
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public class BaseBottomSheetDialogFragment<B extends ViewDataBinding> extends DialogFragment {
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private BaseBottomSheetDialog<B> dialog = null;
    private WeakReference<Bundle> savedInstanceState = null;

    @Override
    public int getTheme() {
        return R.style.Theme_Dialog_Base_BottomDialog;
    }

    @CallSuper
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = new WeakReference<>(savedInstanceState);
        dialog = new BaseBottomSheetDialog<B>(requireContext(), getTheme()) {
        };
        return dialog;
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onStart() {
        if (dialog.binding == null) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) getClass().getGenericSuperclass();
            if (parameterizedType != null) {
                try {
                    Method method = ((Class<?>) parameterizedType.getActualTypeArguments()[0])
                            .getMethod("inflate", LayoutInflater.class);
                    dialog.binding = (B) Objects.requireNonNull(
                            method.invoke(null, getLayoutInflater()));
                } catch (NoSuchMethodException
                        | InvocationTargetException
                        | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        super.onStart();
        onDialogCreated(dialog, savedInstanceState == null ? null : savedInstanceState.get());
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setDimAmount(getDimAmount());
            }
        }
    }

    @CallSuper
    public void onDialogCreated(BaseBottomSheetDialog<B> dialog,
                                @Nullable Bundle savedInstanceState) {
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
        show(fragment.requireFragmentManager());
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
     * @return 창 밖 배경 어두운 정도
     */
    protected float getDimAmount() {
        return 0.6f;
    }

    /**
     * 현재 적용된 테마 반환
     *
     * @return 테마
     * @see ThemeType
     */
    @ThemeType
    public int getApplicationTheme() {
        return themeHelpers.getCurrentTheme();
    }

    /**
     * 앱 테마 적용
     *
     * @param type 테마
     * @see ThemeType
     */
    public void applyApplicationTheme(@ThemeType int type) {
        themeHelpers.applyTheme(type);
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

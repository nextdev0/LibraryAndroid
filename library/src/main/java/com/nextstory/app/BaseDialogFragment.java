package com.nextstory.app;

import android.app.Dialog;
import android.os.Bundle;
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
import com.nextstory.app.locale.LocaleManager;
import com.nextstory.app.locale.LocaleManagerImpl;
import com.nextstory.app.theme.ThemeHelpers;
import com.nextstory.app.theme.ThemeType;
import com.nextstory.util.Unsafe;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 기본 다이얼로그 프래그먼트
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseDialogFragment<B extends ViewDataBinding> extends DialogFragment {
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final LocaleManager localeManager =
            new LocaleManagerImpl(() -> requireContext().getApplicationContext());
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
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setDimAmount(getDimAmount());
            }
        }
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
     * 아무동작 없음 (데이터바인딩시 창 클릭시 닫기지 않도록 하기 위함)
     */
    public final void nothing() {
        // no-op
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
     * 지원되는 로케일 목록 지정
     *
     * @param locales 로케일 목록
     * @since 1.3
     */
    public void registerSupportedLocales(List<Locale> locales) {
        localeManager.registerSupportedLocales(locales);
    }

    /**
     * @return 현재 로케일
     */
    @NonNull
    public Locale getLocale() {
        return localeManager.getLocale();
    }

    /**
     * 로케일 적용
     *
     * @param locale 로케일
     */
    public void applyLocale(@NonNull Locale locale) {
        localeManager.applyLocale(Objects.requireNonNull(locale));
    }

    /**
     * 반투명 테마 적용
     */
    public void applyTranslucentTheme() {
        dialog.applyTranslucentTheme();
    }

    /**
     * 투명 테마 적용
     */
    public void applyTransparentTheme() {
        dialog.applyTransparentTheme();
    }

    /**
     * 상태바 밝음 유무 상태를 설정함
     *
     * @param enabled 활성화 유무
     */
    @SuppressWarnings("SameParameterValue")
    public void applyLightStatusBar(boolean enabled) {
        dialog.applyLightStatusBar(enabled);
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

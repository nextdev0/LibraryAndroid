package com.nextstory.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.databinding.ViewDataBinding;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nextstory.R;
import com.nextstory.util.theme.ThemeHelpers;
import com.nextstory.util.theme.ThemeType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 기본 바텀시트 다이얼로그
 *
 * @author troy
 * @version 1.0.2
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public abstract class BaseBottomSheetDialog<B extends ViewDataBinding> extends BottomSheetDialog {
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private FrameLayout viewContainer;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    B binding = null;

    public BaseBottomSheetDialog(@NonNull Context context) {
        this(context, R.style.Theme_Dialog_Base_BottomDialog);
    }

    public BaseBottomSheetDialog(@NonNull Context context, @StyleRes int themeRes) {
        super(context, themeRes);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_bottom_sheet_base);
        viewContainer = findViewById(R.id.view_container);
        if (binding == null && savedInstanceState == null) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) getClass().getGenericSuperclass();
            if (parameterizedType != null) {
                try {
                    Method method = ((Class<?>) parameterizedType.getActualTypeArguments()[0])
                            .getMethod("inflate", LayoutInflater.class);
                    binding = (B) method.invoke(null, LayoutInflater.from(getContext()));
                    if (binding == null) {
                        throw new NullPointerException();
                    }
                } catch (NoSuchMethodException
                        | InvocationTargetException
                        | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        if (binding != null) {
            setContentView(binding.getRoot());
        }
    }

    @CallSuper
    @Override
    protected void onStart() {
        Window window = getWindow();
        if (window != null) {
            window.setDimAmount(getDimAmount());
        }
        super.onStart();
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(int layoutResId) {
        View view = View.inflate(getContext(), layoutResId, null);
        setContentView(view);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(View view) {
        setContentView(view, null);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(View view, ViewGroup.LayoutParams params) {
        viewContainer.removeAllViews();
        viewContainer.addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    public void dismiss() {
        binding = null;
        super.dismiss();
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
     * 아무동작 없음 (데이터바인딩시 창 클릭시 닫기지 않도록 하기 위함)
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
        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return 뷰 바인딩 인스턴스
     */
    @CallSuper
    protected B getBinding() {
        return binding;
    }
}

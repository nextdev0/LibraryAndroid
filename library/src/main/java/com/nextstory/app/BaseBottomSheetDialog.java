package com.nextstory.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nextstory.util.Unsafe;

/**
 * 기본 바텀시트 다이얼로그
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public abstract class BaseBottomSheetDialog<B extends ViewDataBinding> extends BottomSheetDialog {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    B binding = null;

    private FrameLayout viewContainer;

    public BaseBottomSheetDialog(@NonNull Context context) {
        this(context, R.style.Theme_Dialog_Base_BottomDialog);
    }

    public BaseBottomSheetDialog(@NonNull Context context, @StyleRes int themeRes) {
        super(context, themeRes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_bottom_sheet_base);
        viewContainer = findViewById(R.id.view_container);
        if (binding == null && savedInstanceState == null) {
            Class<?> klass = Unsafe.getGenericClass(this, 0);
            if (klass != null) {
                binding = Unsafe.invoke(klass, "inflate", LayoutInflater.from(getContext()));
            }
        }
        if (binding != null) {
            setContentView(binding.getRoot());
        }
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

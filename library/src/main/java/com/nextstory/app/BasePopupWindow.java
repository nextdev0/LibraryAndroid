package com.nextstory.app;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.nextstory.app.locale.LocaleManager;
import com.nextstory.app.locale.LocaleManagerImpl;
import com.nextstory.app.theme.ThemeHelpers;
import com.nextstory.app.theme.ThemeType;
import com.nextstory.util.Unsafe;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 기본 팝업창 프래그먼트
 *
 * @author troy
 * @since 1.3
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedReturnValue"})
public abstract class BasePopupWindow<B extends ViewDataBinding> {
    private final Context context;
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final LocaleManager localeManager =
            new LocaleManagerImpl(() -> getContext().getApplicationContext());

    private B binding = null;
    private PopupWindow popupWindow = null;
    private boolean isShowing = false;
    private int animationStyle = -1;
    private int gravity = Gravity.CENTER_HORIZONTAL;
    private boolean focusable = true;
    private boolean outsideTouchable = true;

    public BasePopupWindow(@NonNull Context context) {
        this.context = context;
    }

    @CallSuper
    protected void onCreate(PopupWindow popupWindow) {
        // no-op
    }

    /**
     * @return 컨텍스트
     */
    public final Context getContext() {
        return context;
    }

    /**
     * 창 열기
     *
     * @param anchorView 창이 열릴 위치를 참조할 뷰
     */
    @CallSuper
    public void show(View anchorView) {
        if (!isShowing) {
            isShowing = true;
            if (binding == null) {
                Class<?> klass = Unsafe.getGenericClass(this, 0);
                if (klass != null) {
                    binding = Objects.requireNonNull(
                            Unsafe.invoke(klass, "inflate", LayoutInflater.from(context)));
                }
            }
            popupWindow = new PopupWindow();
            popupWindow.setAnimationStyle(animationStyle);
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(outsideTouchable);
            popupWindow.setFocusable(focusable);
            popupWindow.setBackgroundDrawable(binding.getRoot().getBackground());
            popupWindow.setContentView(binding.getRoot());
            popupWindow.setElevation(binding.getRoot().getElevation());
            popupWindow.setContentView(binding.getRoot());
            popupWindow.showAsDropDown(anchorView, 0, 0, gravity);
            onCreate(popupWindow);
        }
    }

    /**
     * 창닫기
     */
    @CallSuper
    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        popupWindow = null;
        isShowing = false;
    }

    /**
     * 아무동작 없음 (데이터바인딩시 창 클릭시 닫기지 않도록 하기 위함)
     */
    public final void nothing() {
        // no-op
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
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
     * @return 바인딩 인스턴스
     */
    @CallSuper
    protected B getBinding() {
        return binding;
    }

    /**
     * @return 창의 표시 유무
     */
    public final boolean isShowing() {
        return isShowing;
    }

    public BasePopupWindow<B> setAnimationStyle(int animationStyle) {
        this.animationStyle = animationStyle;
        return this;
    }

    public BasePopupWindow<B> setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public BasePopupWindow<B> setFocusable(boolean focusable) {
        this.focusable = focusable;
        return this;
    }

    public BasePopupWindow<B> setOutsideTouchable(boolean outsideTouchable) {
        this.outsideTouchable = outsideTouchable;
        return this;
    }
}

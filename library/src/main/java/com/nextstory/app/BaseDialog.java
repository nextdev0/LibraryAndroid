package com.nextstory.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;
import androidx.databinding.ViewDataBinding;

import com.nextstory.R;
import com.nextstory.util.Unsafe;

import java.util.Objects;

/**
 * 기본 다이얼로그
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public abstract class BaseDialog<B extends ViewDataBinding> extends Dialog {
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  B binding = null;

  private WindowController windowController;
  private ResourcesController resourcesController;

  public BaseDialog(@NonNull Context context) {
    this(context, R.style.Theme_Dialog_Base);
  }

  public BaseDialog(@NonNull Context context, @StyleRes int themeRes) {
    super(context, themeRes);
  }

  @CallSuper
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    windowController = new WindowController(this);
    resourcesController = new ResourcesController(getContext());

    if (binding == null && savedInstanceState == null) {
      Class<?> klass = Unsafe.getGenericClass(this, 0);
      if (klass != null) {
        binding = Unsafe.invoke(klass, "inflate", getLayoutInflater());
      }
    }

    if (binding != null) {
      super.setContentView(binding.getRoot());
    }

    Window window = getWindow();
    if (window != null) {
      window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT);
      window.setStatusBarColor(Color.TRANSPARENT);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      View decorView = window.getDecorView();
      int flags = decorView.getSystemUiVisibility();
      flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      decorView.setSystemUiVisibility(flags);
    }

    ViewGroup contentView = findViewById(android.R.id.content);
    ViewCompat.setOnApplyWindowInsetsListener(contentView, (v, insets) -> {
      if (binding != null) {
        binding.getRoot().setPadding(
          insets.getSystemWindowInsetLeft(),
          0,
          insets.getSystemWindowInsetRight(),
          insets.getSystemWindowInsetBottom());
      }
      return insets.consumeSystemWindowInsets();
    });
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
  public final void setContentView(@NonNull View view) {
    super.setContentView(view);
  }

  @Deprecated
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @Override
  public final void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
  }

  @Deprecated
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @Override
  public final void addContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
    super.addContentView(view, params);
  }

  @CallSuper
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
   * @return 바인딩 인스턴스
   */
  @CallSuper
  protected B getBinding() {
    return binding;
  }

  /**
   * @return 윈도우 설정
   * @since 2.0
   */
  @NonNull
  public final WindowController getWindowController() {
    return Objects.requireNonNull(windowController);
  }

  /**
   * @return 리소스 설정
   * @since 2.0
   */
  @NonNull
  public final ResourcesController getResourcesController() {
    return Objects.requireNonNull(resourcesController);
  }
}

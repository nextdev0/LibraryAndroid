package com.nextstory.libgdx.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.nextstory.libgdx.R;

import java.util.Objects;

/**
 * 윈도우 유틸
 *
 * @author troy
 * @since 2.1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("UnusedDeclaration")
public final class WindowUtils {
  private WindowUtils() {
    // no-op
  }

  /**
   * 전체화면 모드 적용
   *
   * @param window 윈도우
   */
  public static void applyFullscreenMode(@NonNull Window window) {
    Objects.requireNonNull(window);

    ViewGroup contentView = window.getDecorView().findViewById(android.R.id.content);
    contentView.removeView(contentView.findViewById(R.id.translucent_status_bar));

    int flags = window.getDecorView().getSystemUiVisibility();
    flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
      | View.SYSTEM_UI_FLAG_FULLSCREEN
      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
      | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    window.getDecorView().setSystemUiVisibility(flags);
    window.getDecorView().setOnSystemUiVisibilityChangeListener(
      flags1 -> applyFullscreenMode(window));
  }

  /**
   * 상태바 투명화 테마를 적용함.
   *
   * @param window 윈도우 인스턴스
   */
  public static void applyTranslucentTheme(@NonNull Window window) {
    Objects.requireNonNull(window);

    window.setStatusBarColor(Color.TRANSPARENT);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    int flags = window.getDecorView().getSystemUiVisibility();
    flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }
    window.getDecorView().setSystemUiVisibility(flags);

    ViewGroup contentView = window.getDecorView().findViewById(android.R.id.content);
    contentView.post(() -> {
      contentView.removeView(contentView.findViewById(R.id.translucent_status_bar));
      View statusBarView = new View(contentView.getContext());
      statusBarView.setId(R.id.translucent_status_bar);
      statusBarView.setBackgroundColor(0x3f000000);
      contentView.addView(statusBarView,
        new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
    });
  }

  private static int getStatusBarHeight(Window window) {
    if (window == null) {
      return 0;
    }
    Resources resources = window.getDecorView().getResources();
    int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    return (resourceId == 0) ? 0 : resources.getDimensionPixelSize(resourceId);
  }
}

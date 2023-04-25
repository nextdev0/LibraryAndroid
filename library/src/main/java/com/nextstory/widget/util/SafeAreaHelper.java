package com.nextstory.widget.util;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nextstory.util.LibraryInitializer;
import com.nextstory.util.SimpleActivityLifecycleCallbacks;
import com.nextstory.widget.SafeArea;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * {@link SafeArea} 유틸
 *
 * @author troy
 * @since 2.0
 */
@SuppressWarnings("deprecation")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SafeAreaHelper implements LibraryInitializer {
  private static final Rect systemInsets = new Rect();
  private static final Rect currentInsets = new Rect();
  private static final Set<Listener> listeners = new HashSet<>();
  private static final Consumer<Activity> updateConsumer = activity -> {
    Window window = activity.getWindow();
    View decorView = window.getDecorView();

    // 현재 윈도우 플래그 검사
    int flags = window.getDecorView().getSystemUiVisibility();
    int wflags = window.getAttributes().flags;
    boolean isStatusBarHide =
      (flags & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0;
    boolean isNavigationBarHide =
      ((flags | wflags) & FLAG_LAYOUT_NO_LIMITS) != 0;

    // 영역 검사 및 값 전달
    decorView.post(() -> {
      WindowInsetsCompat windowInsets =
        ViewCompat.getRootWindowInsets(decorView);
      if (windowInsets != null) {
        Insets systemBar = windowInsets.getInsets(
          WindowInsetsCompat.Type.systemBars());
        Insets displayCut = windowInsets.getInsets(
          WindowInsetsCompat.Type.displayCutout());
        systemInsets.set(
          Math.max(systemBar.left, displayCut.left),
          Math.max(systemBar.top, displayCut.top),
          Math.max(systemBar.right, displayCut.right),
          Math.max(systemBar.bottom, displayCut.bottom));
        currentInsets.set(
          isNavigationBarHide ? systemInsets.left : 0,
          isStatusBarHide ? systemInsets.top : 0,
          isNavigationBarHide ? systemInsets.right : 0,
          isNavigationBarHide ? systemInsets.bottom : 0);
        for (Listener l : listeners) {
          if (l != null) {
            l.onSafeAreaChanged(currentInsets, systemInsets);
          }
        }
      }
    });
  };

  public static void addListener(Listener l) {
    listeners.add(l);

    if (l != null) {
      l.onSafeAreaChanged(currentInsets, systemInsets);
    }
  }

  public static void removeListener(Listener l) {
    listeners.remove(l);
  }

  @Override
  public void onInitialized(Context context, String argument) {
    ((Application) context).registerActivityLifecycleCallbacks(
      new SimpleActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity,
                                      @Nullable Bundle savedInstanceState) {
          activity.getWindow()
            .getDecorView()
            .getViewTreeObserver()
            .addOnGlobalLayoutListener(
              new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                  if (activity.getWindow()
                    .getDecorView()
                    .getViewTreeObserver().isAlive()) {
                    updateConsumer.accept(activity);
                  } else {
                    activity.getWindow()
                      .getDecorView()
                      .getViewTreeObserver()
                      .removeOnGlobalLayoutListener(this);
                  }
                }
              });
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
          updateConsumer.accept(activity);
        }
      });
  }

  public interface Listener {
    /**
     * 시스템 영역 값 전달 호출
     *
     * @param fitSystemInsets 시스템 영역이 필요한 insets
     * @param systemInsets    시스템 영역 insets
     */
    void onSafeAreaChanged(Rect fitSystemInsets, Rect systemInsets);
  }
}

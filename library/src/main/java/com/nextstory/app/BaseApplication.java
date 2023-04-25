package com.nextstory.app;

import android.app.Application;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nextstory.util.SimpleActivityLifecycleCallbacks;
import com.nextstory.util.SimpleFragmentLifecycleCallbacks;

/**
 * 기본 애플리케이션
 *
 * @author troy
 * @since 1.3
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseApplication extends Application
  implements SimpleActivityLifecycleCallbacks, SimpleFragmentLifecycleCallbacks {
  private final ResourcesController resourcesController = new ResourcesController(this);

  /**
   * @return 리소스 설정
   * @since 2.0
   */
  @NonNull
  public ResourcesController getResourcesController() {
    return resourcesController;
  }

  /**
   * @return 로케일이 적용된 {@link Resources}
   */
  public Resources getLocaleResources() {
    return resourcesController.getLocaleResources();
  }

  /**
   * 토스트 표시
   *
   * @param res 문자열 리소스
   */
  public void showToast(@StringRes int res) {
    Toast.makeText(this, getLocaleResources().getString(res), Toast.LENGTH_SHORT).show();
  }

  /**
   * 토스트 표시
   *
   * @param message 문자열
   */
  public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}

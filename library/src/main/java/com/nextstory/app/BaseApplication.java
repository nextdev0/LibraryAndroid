package com.nextstory.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
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
    private final LocalizationApplicationDelegate localizationApplicationDelegate =
            new LocalizationApplicationDelegate();
    private final ResourcesController resourcesController = new ResourcesController(this);

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            localizationApplicationDelegate.setDefaultLanguage(base,
                    base.getResources().getConfiguration().getLocales().get(0));
        } else {
            localizationApplicationDelegate.setDefaultLanguage(base,
                    base.getResources().getConfiguration().locale);
        }
        super.attachBaseContext(localizationApplicationDelegate.attachBaseContext(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resourcesController.initializeApplication();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationApplicationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationApplicationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationApplicationDelegate.getResources(getBaseContext(), super.getResources());
    }

    /**
     * @return 리소스 설정
     * @since 2.0
     */
    @NonNull
    public ResourcesController getResourcesController() {
        return resourcesController;
    }

    /**
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(this, getString(res), Toast.LENGTH_SHORT).show();
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

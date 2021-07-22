package com.nextstory.app;

import android.app.Service;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * 기본 서비스
 *
 * @author troy
 * @since 1.3
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseService extends Service {
    private final ResourcesController resourcesController = new ResourcesController(this);

    @Override
    public Resources getResources() {
        return resourcesController.getLocaleResources(super.getResources());
    }

    /**
     * @return 리소스 설정
     * @since 2.0
     */
    @NonNull
    public final ResourcesController getResourcesController() {
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

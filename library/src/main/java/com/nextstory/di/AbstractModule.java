package com.nextstory.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * 애플리케이션 객체를 가지는 의존성 모듈
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractModule implements Module {
    private Application application;
    private Activity activity;
    private Fragment fragment;

    void performFragmentBind(@NonNull Fragment fragment) {
        this.fragment = fragment;
        onFragmentBind(fragment);
    }

    void performFragmentUnbind() {
        this.fragment = null;
        onFragmentUnbind();
    }

    void performActivityBind(@NonNull Activity activity) {
        this.activity = activity;
        onActivityBind(activity);
    }

    void performActivityUnbind() {
        this.activity = null;
        onActivityUnbind();
    }

    void performApplicationBind(@NonNull Application application) {
        this.application = application;
        onApplicationBind(application);
    }

    void performApplicationUnbind() {
        this.application = null;
        onApplicationUnbind();
    }

    protected void onApplicationBind(@NonNull Application application) {
        // no-op
    }

    protected void onApplicationUnbind() {
        // no-op
    }

    protected void onActivityBind(@NonNull Activity activity) {
        // no-op
    }

    protected void onActivityUnbind() {
        // no-op
    }

    protected void onFragmentBind(@NonNull Fragment fragment) {
        // no-op
    }

    protected void onFragmentUnbind() {
        // no-op
    }

    @NonNull
    public Application getApplication() {
        return Objects.requireNonNull(application);
    }

    @NonNull
    public Context getContext() {
        if (activity != null) {
            return Objects.requireNonNull(activity);
        }
        return Objects.requireNonNull(fragment).requireContext();
    }
}

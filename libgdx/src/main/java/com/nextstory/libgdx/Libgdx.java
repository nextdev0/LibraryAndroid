package com.nextstory.libgdx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.badlogic.gdx.ApplicationListener;
import com.nextstory.libgdx.core.LibgdxActivity;
import com.nextstory.libgdx.util.WindowUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Libgdx
 *
 * @author troy
 * @since 2.1
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class Libgdx implements ApplicationListener, LifecycleOwner, LifecycleObserver {
    private FragmentActivity activity = null;

    /**
     * libgdx 액티비티 Intent 생성
     *
     * @param context     컨텍스트
     * @param libgdxClass 게임 클래스
     * @return Intent
     */
    @NonNull
    public static Intent createIntent(@NonNull Context context,
                                      @NonNull Class<? extends Libgdx> libgdxClass) {
        return LibgdxActivity.createIntent(context, libgdxClass);
    }

    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        return Objects.requireNonNull(activity.getLifecycle());
    }

    /**
     * @return 화면 방향 수직 유무
     */
    public abstract boolean isScreenOrientationPortrait();

    /**
     * @return 전체화면 유무
     */
    public abstract boolean isFullscreen();

    /**
     * libgdx가 지원하지 않는 경우 호출
     */
    public abstract void onLibgdxNotSupported();

    /**
     * @return 필요 권한 목록
     */
    @CallSuper
    public List<String> getRequirePermissions() {
        return new ArrayList<>();
    }

    /**
     * 필요 권한 거부 시 호출
     *
     * @param deniedPermissions 거부된 권한
     */
    @CallSuper
    public void onRequirePermissionDenied(List<String> deniedPermissions) {
        // no-op
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @CallSuper
    public void attachActivity(@NonNull FragmentActivity activity) {
        this.activity = Objects.requireNonNull(activity);
        this.getLifecycle().addObserver(this);

        if (isFullscreen()) {
            WindowUtils.applyFullscreenMode(activity.getWindow());
        } else {
            WindowUtils.applyTranslucentTheme(activity.getWindow());
        }

        this.activity.setRequestedOrientation(isScreenOrientationPortrait()
                ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * @return AR 뷰 위에 표시할 뷰 생성
     */
    public View onCreateSurfaceUIView() {
        return null;
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected void onCreate() {
        // no-op
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart() {
        // no-op
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        // no-op
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        // no-op
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        // no-op
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        if (activity != null) {
            activity.getLifecycle().removeObserver(this);
            activity = null;
        }
    }

    @CallSuper
    @Override
    public void create() {
        // no-op
    }

    @CallSuper
    @Override
    public void resize(int width, int height) {
        // no-op
    }

    @CallSuper
    @Override
    public void render() {
        // no-op
    }

    @CallSuper
    @Override
    public void pause() {
        // no-op
    }

    @CallSuper
    @Override
    public void resume() {
        // no-op
    }

    @CallSuper
    @Override
    public void dispose() {
        // no-op
    }

    protected Context getContext() {
        return activity;
    }

    protected Context requireContext() {
        return Objects.requireNonNull(activity);
    }
}

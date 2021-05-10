package com.nextstory.app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.nextstory.ar.R;

/**
 * libgdx 액티비티
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class LibGdxActivity extends AbstractBaseActivity implements ApplicationListener {
    protected static final int PERMISSION_REQUEST_CODE = 1000;
    private ViewGroup layoutOverlayContainer = null;
    private boolean isLibGdxSupported = false;

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_internal_ar);

        layoutOverlayContainer = findViewById(R.id.layout_overlay_container);

        // libGdx native 라이브러리 로딩
        // (기기 지원 유무를 확인하기 위하여 임의 로딩)
        try {
            (new SharedLibraryLoader()).load("gdx");
            isLibGdxSupported = true;
        } catch (UnsatisfiedLinkError ignore) {
            isLibGdxSupported = false;
            onNotSupportedDevice();
        }

        // 프래그먼트 설정
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.libgdx_fragment_container, new LibGdxFragment())
                .commit();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        layoutOverlayContainer = null;
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void setContentView(int layoutResID) {
        View contentView = View.inflate(this, layoutResID, null);
        setContentView(contentView);
    }

    @CallSuper
    @Override
    public void setContentView(View view) {
        setContentView(view, null);
    }

    @CallSuper
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        layoutOverlayContainer.removeAllViews();
        if (params == null) {
            layoutOverlayContainer.addView(view);
        } else {
            layoutOverlayContainer.addView(view, params);
        }
    }

    @CallSuper
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        layoutOverlayContainer.addView(view, params);
    }

    /**
     * 미지원 기기일 경우 호출
     */
    @CallSuper
    protected void onNotSupportedDevice() {
        // no-op
    }

    @CallSuper
    @Override
    public final void create() {
        // no-op
    }

    @CallSuper
    @Override
    public final void resize(int width, int height) {
        // no-op
    }

    @CallSuper
    @Override
    public final void render() {
        // no-op
    }

    @CallSuper
    @Override
    public final void pause() {
        // no-op
    }

    @CallSuper
    @Override
    public final void resume() {
        // no-op
    }

    @CallSuper
    @Override
    public final void dispose() {
        // no-op
    }

    /**
     * @return libGdx 지원 유무
     */
    public final boolean isLibGdxSupported() {
        return isLibGdxSupported;
    }
}

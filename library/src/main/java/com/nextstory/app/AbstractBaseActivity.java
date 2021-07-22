package com.nextstory.app;

import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.Objects;

/**
 * 기본 액티비티
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBaseActivity extends AppCompatActivity {
    private final WindowController windowController = new WindowController(this);
    private final ResourcesController resourcesController = new ResourcesController(this);

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final PointF touchPoint = new PointF(0f, 0f);
    private boolean isFocused = false;

    @ResourcesController.ThemeType
    private int currentAppTheme;
    private Locale currentLocale;

    private InputMethodManager inputMethodManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAppTheme = resourcesController.getAppTheme();
        currentLocale = resourcesController.getLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 테마 변경 시 액티비티 재생성
        if (currentAppTheme != resourcesController.getAppTheme()) {
            recreate();
        }

        // 로케일 변경 시 액티비티 재생성
        if (currentLocale != resourcesController.getLocale()) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        inputMethodManager = null;
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 전체화면일 경우 터치시마다 테마를 재적용함.
        if (getWindow() != null) {
            int windowType = Objects.requireNonNull(windowController.getSettings()).getWindowType();
            if (windowType == WindowController.TYPE_FULLSCREEN) {
                windowController.applyWindowType(WindowController.TYPE_FULLSCREEN);
            }
        }

        View view = getCurrentFocus();
        if (view != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                touchPoint.set(ev.getX(), ev.getY());
                return super.dispatchTouchEvent(ev);
            }
            if (ev.getAction() != MotionEvent.ACTION_UP) {
                return super.dispatchTouchEvent(ev);
            }
            if (view instanceof EditText) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);

                // 스크롤 인식 체크
                float dpi = view.getResources().getDisplayMetrics().density;
                int distance = (int) (dpi * 24);
                if (Math.abs(ev.getX() - touchPoint.x) > distance
                        || Math.abs(ev.getY() - touchPoint.y) > distance) {
                    return super.dispatchTouchEvent(ev);
                }

                // 포커스된 뷰 위치 체크
                if (ev.getX() >= rect.left && ev.getX() <= rect.right
                        && ev.getY() >= rect.top && ev.getY() <= rect.bottom) {
                    return super.dispatchTouchEvent(ev);
                }
                view.getViewTreeObserver().addOnGlobalFocusChangeListener(
                        new ViewTreeObserver.OnGlobalFocusChangeListener() {
                            @Override
                            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                                view.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
                                if (newFocus != null) {
                                    isFocused = true;
                                }
                            }
                        });
                mainThreadHandler.postDelayed(() -> {
                    if (inputMethodManager == null) {
                        inputMethodManager = ContextCompat.getSystemService(
                                this, InputMethodManager.class);
                    }
                    if (!isFocused && inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        view.clearFocus();
                    }
                    isFocused = false;
                }, 70);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public Resources getResources() {
        return resourcesController.getLocaleResources(super.getResources());
    }

    /**
     * @return 윈도우 설정
     * @since 2.0
     */
    @NonNull
    public final WindowController getWindowController() {
        return windowController;
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

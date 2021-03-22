package com.nextstory.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.nextstory.R;
import com.nextstory.util.permission.PermissionHelpers;
import com.nextstory.util.permission.PermissionListener;
import com.nextstory.util.theme.ThemeHelpers;
import com.nextstory.util.theme.ThemeType;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 기본 액티비티
 *
 * @author troy
 * @version 1.0.1
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBaseActivity
        extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final PermissionHelpers permissionHelpers = new PermissionHelpers(this);
    private final PointF touchPoint = new PointF(0f, 0f);
    private final CompositeDisposable onPauseDisposables = new CompositeDisposable();
    private final CompositeDisposable onStopDisposables = new CompositeDisposable();
    private final CompositeDisposable onDestroyDisposables = new CompositeDisposable();
    private boolean isFocused = false;
    private View decorView = null;
    private View contentView = null;
    private ViewTreeObserver viewTreeObserver = null;
    private InputMethodManager inputMethodManager = null;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelpers.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        decorView = getWindow().getDecorView();
        contentView = getWindow().findViewById(android.R.id.content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int mode = getWindow().getAttributes().softInputMode;
        if ((mode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0) {
            if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
                viewTreeObserver = contentView.getViewTreeObserver();
            }
            viewTreeObserver.addOnGlobalLayoutListener(this);
        }
    }

    @Override
    protected void onPause() {
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(this);
        }
        onPauseDisposables.clear();
        super.onPause();
    }

    @Override
    protected void onStop() {
        onStopDisposables.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        onDestroyDisposables.clear();
        viewTreeObserver = null;
        decorView = null;
        contentView = null;
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final View view = getCurrentFocus();
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
    public void onGlobalLayout() {
        Rect r = new Rect();
        decorView.getWindowVisibleDisplayFrame(r);
        int height = decorView.getContext().getResources().getDisplayMetrics().heightPixels;
        int diff = height - r.bottom;
        if (diff != 0) {
            if (contentView.getPaddingBottom() != diff) {
                contentView.setPadding(0, 0, 0, diff);
            }
        } else {
            if (contentView.getPaddingBottom() != 0) {
                contentView.setPadding(0, 0, 0, 0);
            }
        }
    }

    /**
     * 현재 적용된 테마 반환
     *
     * @return 테마
     * @see ThemeType
     */
    @ThemeType
    public int getApplicationTheme() {
        return themeHelpers.getCurrentTheme();
    }

    /**
     * 앱 테마 적용
     *
     * @param type 테마
     * @see ThemeType
     */
    public void applyApplicationTheme(@ThemeType int type) {
        themeHelpers.applyTheme(type);
    }

    /**
     * 반투명 테마 적용
     */
    public void applyTranslucentTheme() {
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flags = window.getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(flags);
            contentView.post(() -> {
                if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                    View statusBarView = new View(contentView.getContext());
                    statusBarView.setId(R.id.translucent_status_bar);
                    statusBarView.setBackgroundColor(0x3f000000);
                    contentView.addView(statusBarView,
                            new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
                }
            });
        }
        applyLightStatusBar(false);
    }

    /**
     * 투명 테마 적용
     */
    public void applyTransparentTheme() {
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            View statusBar = contentView.findViewById(R.id.translucent_status_bar);
            if (statusBar != null) {
                contentView.removeView(statusBar);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flags = window.getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                contentView.post(() -> {
                    if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                        View statusBarView = new View(contentView.getContext());
                        statusBarView.setId(R.id.translucent_status_bar);
                        statusBarView.setBackgroundColor(0x3f000000);
                        contentView.addView(statusBarView,
                                new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
                    }
                });
            }
        }
    }

    /**
     * 상태바 밝음 유무 상태를 설정함
     *
     * @param enabled 활성화 유무
     */
    @SuppressWarnings("SameParameterValue")
    public void applyLightStatusBar(boolean enabled) {
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            View decorView = window.getDecorView();
            decorView.postDelayed(() -> {
                int flags = decorView.getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (enabled) {
                        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                }
                decorView.setSystemUiVisibility(flags);
            }, 250L);
        }
    }

    /**
     * 상태바 높이를 반환
     *
     * @param window 윈도우 인스턴스
     * @return 높이값, 윈도우가 {@code null}일 경우 {@code 0}을 반환
     */
    private int getStatusBarHeight(Window window) {
        if (window == null) {
            return 0;
        }
        Resources resources = window.getDecorView().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId == 0) ? 0 : resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 권한 요청 근거 표시 유무 반환
     *
     * @param permissions 필요 권한
     * @return 표시 유무
     */
    public boolean isNeedShowRequestPermissionRationale(String... permissions) {
        return permissionHelpers.isNeedShowRequestPermissionRationale(permissions);
    }

    /**
     * 권한 허용 유무 반환
     *
     * @param permissions 권한
     * @return 허용 유무
     */
    public boolean isPermissionGranted(String... permissions) {
        return permissionHelpers.isPermissionGranted(permissions);
    }

    /**
     * 권한 요청
     *
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     * @param permissions      요청 권한
     */
    public void requestPermissions(@Nullable PermissionListener permissionResult,
                                   @NonNull String... permissions) {
        permissionHelpers.requestPermissions(permissionResult, permissions);
    }

    /**
     * 권한 요청
     *
     * @param permissions      요청 권한
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     */
    public void requestPermissions(@NonNull String[] permissions,
                                   @Nullable PermissionListener permissionResult) {
        permissionHelpers.requestPermissions(permissions, permissionResult);
    }

    /**
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 작업 추가, 기본으로 수명주기가 {@link Lifecycle.Event#ON_DESTROY} 해제됨.
     *
     * @param disposable 작업
     */
    public void addDisposable(@NonNull Disposable disposable) {
        addDisposable(Lifecycle.Event.ON_DESTROY, disposable);
    }

    /**
     * 작업 추가
     *
     * @param lifecycleEvent 작업 해제될 수명주기 이벤트
     * @param disposable     작업
     */
    @SuppressWarnings("SameParameterValue")
    public void addDisposable(@NonNull Lifecycle.Event lifecycleEvent,
                              @NonNull Disposable disposable) {
        if (lifecycleEvent == Lifecycle.Event.ON_PAUSE) {
            onPauseDisposables.add(disposable);
        } else if (lifecycleEvent == Lifecycle.Event.ON_STOP) {
            onStopDisposables.add(disposable);
        } else if (lifecycleEvent == Lifecycle.Event.ON_DESTROY) {
            onDestroyDisposables.add(disposable);
        }
    }

    /**
     * @return 메인 스레드 핸들러
     */
    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
}

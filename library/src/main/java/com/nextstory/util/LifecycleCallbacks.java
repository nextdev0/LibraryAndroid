package com.nextstory.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 수명주기 관련 유틸 클래스
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public final class LifecycleCallbacks
        implements LibraryInitializer, SimpleActivityLifecycleCallbacks {
    private static final Set<Application.ActivityLifecycleCallbacks> activityCallbacks =
            Collections.synchronizedSet(new HashSet<>());
    private static final Set<FragmentManager.FragmentLifecycleCallbacks> fragmentCallbacks =
            Collections.synchronizedSet(new HashSet<>());
    private static final Set<SimpleFragmentLifecycleCallbacks> simpleFragmentCallbacks =
            Collections.synchronizedSet(new HashSet<>());

    /**
     * 프래그먼트 콜백 처리
     */
    private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentPreAttached(@NonNull FragmentManager fragmentManager,
                                          @NonNull Fragment fragment,
                                          @NonNull Context context) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentPreAttached(fragmentManager, fragment, context);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentPreAttached(fragmentManager, fragment, context);
            }
        }

        @Override
        public void onFragmentAttached(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment,
                                       @NonNull Context context) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentAttached(fragmentManager, fragment, context);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentAttached(fragmentManager, fragment, context);
            }
        }

        @Override
        public void onFragmentPreCreated(@NonNull FragmentManager fragmentManager,
                                         @NonNull Fragment fragment,
                                         @Nullable Bundle savedInstanceState) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentPreCreated(fragmentManager, fragment, savedInstanceState);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentPreCreated(fragmentManager, fragment, savedInstanceState);
            }
        }

        @Override
        public void onFragmentCreated(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment,
                                      @Nullable Bundle savedInstanceState) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentCreated(fragmentManager, fragment, savedInstanceState);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentCreated(fragmentManager, fragment, savedInstanceState);
            }
        }

        @Deprecated
        @Override
        public void onFragmentActivityCreated(@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment,
                                              @Nullable Bundle savedInstanceState) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentActivityCreated(fragmentManager, fragment, savedInstanceState);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentActivityCreated(fragmentManager, fragment, savedInstanceState);
            }
        }

        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fragmentManager,
                                          @NonNull Fragment fragment,
                                          @NonNull View v,
                                          @Nullable Bundle savedInstanceState) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentViewCreated(fragmentManager, fragment, v, savedInstanceState);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentViewCreated(fragmentManager, fragment, v, savedInstanceState);
            }
        }

        @Override
        public void onFragmentStarted(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentStarted(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentStarted(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentResumed(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentStarted(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentStarted(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentPaused(@NonNull FragmentManager fragmentManager,
                                     @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentPaused(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentPaused(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentStopped(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentStopped(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentStopped(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentSaveInstanceState(@NonNull FragmentManager fragmentManager,
                                                @NonNull Fragment fragment,
                                                @NonNull Bundle outState) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentSaveInstanceState(fragmentManager, fragment, outState);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentSaveInstanceState(fragmentManager, fragment, outState);
            }
        }

        @Override
        public void onFragmentViewDestroyed(@NonNull FragmentManager fragmentManager,
                                            @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentViewDestroyed(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentViewDestroyed(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fragmentManager,
                                        @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentDestroyed(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentDestroyed(fragmentManager, fragment);
            }
        }

        @Override
        public void onFragmentDetached(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment) {
            for (FragmentManager.FragmentLifecycleCallbacks callback : fragmentCallbacks) {
                callback.onFragmentDetached(fragmentManager, fragment);
            }
            for (SimpleFragmentLifecycleCallbacks callback : simpleFragmentCallbacks) {
                callback.onFragmentDetached(fragmentManager, fragment);
            }
        }
    };

    /**
     * 액티비티 수명주기 콜백 등록
     *
     * @param callbacks 콜백
     */
    public static void registerActivityLifecycleCallbacks(
            @NonNull Application.ActivityLifecycleCallbacks callbacks
    ) {
        activityCallbacks.add(callbacks);
    }

    /**
     * 액티비티 수명주기 콜백 해제
     *
     * @param callbacks 콜백
     */
    public static void unregisterActivityLifecycleCallbacks(
            @NonNull Application.ActivityLifecycleCallbacks callbacks
    ) {
        activityCallbacks.remove(callbacks);
    }

    /**
     * 프래그먼트 수명주기 콜백 등록
     *
     * @param callbacks 콜백
     */
    public static void registerFragmentLifecycleCallbacks(
            @NonNull FragmentManager.FragmentLifecycleCallbacks callbacks
    ) {
        fragmentCallbacks.add(callbacks);
    }

    /**
     * 프래그먼트 수명주기 콜백 해제
     *
     * @param callbacks 콜백
     */
    public static void unregisterFragmentLifecycleCallbacks(
            @NonNull FragmentManager.FragmentLifecycleCallbacks callbacks
    ) {
        fragmentCallbacks.remove(callbacks);
    }

    /**
     * 프래그먼트 수명주기 콜백 등록
     *
     * @param callbacks 콜백
     */
    public static void registerFragmentLifecycleCallbacks(
            @NonNull SimpleFragmentLifecycleCallbacks callbacks
    ) {
        simpleFragmentCallbacks.add(callbacks);
    }

    /**
     * 프래그먼트 수명주기 콜백 해제
     *
     * @param callbacks 콜백
     */
    public static void unregisterFragmentLifecycleCallbacks(
            @NonNull SimpleFragmentLifecycleCallbacks callbacks
    ) {
        simpleFragmentCallbacks.remove(callbacks);
    }

    @Override
    public void onInitialized(Context context, String argument) {
        Application application = (Application) context;
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityCreated(activity, savedInstanceState);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityStarted(activity);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityResumed(activity);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityPaused(activity);
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityStopped(activity);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
        }
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivityDestroyed(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
            callback.onActivitySaveInstanceState(activity, outState);
        }
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity,
                                     @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreCreated(activity, savedInstanceState);
            }
        }
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity,
                                      @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostCreated(activity, savedInstanceState);
            }
        }
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreStarted(activity);
            }
        }
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostStarted(activity);
            }
        }
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreResumed(activity);
            }
        }
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostResumed(activity);
            }
        }
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPrePaused(activity);
            }
        }
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostPaused(activity);
            }
        }
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreStopped(activity);
            }
        }
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostStopped(activity);
            }
        }
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity,
                                               @NonNull Bundle outState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreSaveInstanceState(activity, outState);
            }
        }
    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity,
                                                @NonNull Bundle outState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostSaveInstanceState(activity, outState);
            }
        }
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPreDestroyed(activity);
            }
        }
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (Application.ActivityLifecycleCallbacks callback : activityCallbacks) {
                callback.onActivityPostDestroyed(activity);
            }
        }
    }
}

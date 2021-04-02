package com.nextstory.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
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
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public final class LifecycleCallbacks implements SimpleActivityLifecycleCallbacks {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    static Application sApplication = null;

    /**
     * 프래그먼트 콜백
     */
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
        if (sApplication != null) {
            sApplication.registerActivityLifecycleCallbacks(callbacks);
        }
    }

    /**
     * 액티비티 수명주기 콜백 해제
     *
     * @param callbacks 콜백
     */
    public static void unregisterActivityLifecycleCallbacks(
            @NonNull Application.ActivityLifecycleCallbacks callbacks
    ) {
        if (sApplication != null) {
            sApplication.unregisterActivityLifecycleCallbacks(callbacks);
        }
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
    public void onActivityPreCreated(@NonNull Activity activity,
                                     @Nullable Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
        }
    }
}

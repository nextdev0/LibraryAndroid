package com.nextstory.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * {@link FragmentManager.FragmentLifecycleCallbacks} 간소화 인터페이스
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public interface SimpleFragmentLifecycleCallbacks {
    default void onFragmentPreAttached(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment,
                                       @NonNull Context context) {
        // no-op
    }

    default void onFragmentAttached(@NonNull FragmentManager fragmentManager,
                                    @NonNull Fragment fragment,
                                    @NonNull Context context) {
        // no-op
    }

    default void onFragmentPreCreated(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment,
                                      @Nullable Bundle savedInstanceState) {
        // no-op
    }

    default void onFragmentCreated(@NonNull FragmentManager fragmentManager,
                                   @NonNull Fragment fragment,
                                   @Nullable Bundle savedInstanceState) {
        // no-op
    }

    default void onFragmentActivityCreated(@NonNull FragmentManager fragmentManager,
                                           @NonNull Fragment fragment,
                                           @Nullable Bundle savedInstanceState) {
        // no-op
    }

    default void onFragmentViewCreated(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment,
                                       @NonNull View v,
                                       @Nullable Bundle savedInstanceState) {
        // no-op
    }

    default void onFragmentStarted(@NonNull FragmentManager fragmentManager,
                                   @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentResumed(@NonNull FragmentManager fragmentManager,
                                   @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentPaused(@NonNull FragmentManager fragmentManager,
                                  @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentStopped(@NonNull FragmentManager fragmentManager,
                                   @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentSaveInstanceState(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment,
                                             @NonNull Bundle outState) {
        // no-op
    }

    default void onFragmentViewDestroyed(@NonNull FragmentManager fragmentManager,
                                         @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentDestroyed(@NonNull FragmentManager fragmentManager,
                                     @NonNull Fragment fragment) {
        // no-op
    }

    default void onFragmentDetached(@NonNull FragmentManager fragmentManager,
                                    @NonNull Fragment fragment) {
        // no-op
    }
}

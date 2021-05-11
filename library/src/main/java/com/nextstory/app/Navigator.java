package com.nextstory.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.nextstory.R;
import com.nextstory.util.LibraryInitializer;
import com.nextstory.util.LifecycleCallbacks;
import com.nextstory.util.SimpleActivityLifecycleCallbacks;
import com.nextstory.util.SimpleFragmentLifecycleCallbacks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 내비게이터
 *
 * @author troy
 * @since 1.5
 */
@SuppressWarnings("UnusedDeclaration")
public final class Navigator implements
        LibraryInitializer,
        SimpleFragmentLifecycleCallbacks,
        SimpleActivityLifecycleCallbacks {
    private static final Map<FragmentManager, Integer> containerIds = new LinkedHashMap<>();
    private static final Settings settings = new Settings();

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Navigator() {
        // no-op
    }

    @Override
    public void onInitialized(Context context, String argument) {
        LifecycleCallbacks.registerActivityLifecycleCallbacks(this);
        LifecycleCallbacks.registerFragmentLifecycleCallbacks(this);
    }

    @Override
    public void onFragmentAttached(@NonNull FragmentManager fragmentManager,
                                   @NonNull Fragment fragment,
                                   @NonNull Context context) {
        if (!containerIds.containsKey(fragmentManager)) {
            int fragmentId = fragment.getId();
            containerIds.put(fragmentManager, fragmentId);
        }
        fragment.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void onDestroy() {
                containerIds.remove(fragment.getChildFragmentManager());
                fragment.getLifecycle().removeObserver(this);
            }
        });
    }

    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment,
                                      @NonNull View v,
                                      @Nullable Bundle savedInstanceState) {
        v.setTranslationZ(fragmentManager.getBackStackEntryCount());
        v.setOutlineProvider(null);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            containerIds.remove(fragmentManager);
        }
    }

    private static String getFragmentTag(int fragmentContainerId,
                                         FragmentManager fragmentManager,
                                         Fragment newFragment,
                                         boolean isNavigated) {
        // "navigator::{current fragment name}#{fragmentId}:{backstack number}"
        return "navigator::"
                + newFragment.getClass().getSimpleName()
                + '#'
                + fragmentContainerId
                + ':'
                + (fragmentManager.getBackStackEntryCount() + (isNavigated ? 1 : 0));
    }

    /**
     * @return 내비게이터 설정
     */
    public static Settings getSettings() {
        return settings;
    }

    /**
     * 액티비티 이동
     *
     * @param activity 현재 액티비티
     * @param intent   intent
     */
    public static void push(@NonNull Activity activity, Intent intent) {
        Objects.requireNonNull(activity, "activity == null");
        Objects.requireNonNull(intent, "intent == null");
        activity.startActivity(intent);
    }

    /**
     * 프래그먼트 이동
     *
     * @param currentFragment 현재 프래그먼트
     * @param newFragment     이동할 프래그먼트
     */
    public static void push(@NonNull Fragment currentFragment, @NonNull Fragment newFragment) {
        Objects.requireNonNull(currentFragment, "currentFragment == null");
        Objects.requireNonNull(newFragment, "newFragment == null");

        FragmentManager fragmentManager = currentFragment.getParentFragmentManager();
        int fragmentContainerId = Objects.requireNonNull(containerIds.get(fragmentManager));
        String tag = getFragmentTag(fragmentContainerId, fragmentManager, newFragment, true);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        settings.getEnterAnimId(),
                        settings.getExitAnimId(),
                        settings.getPopEnterAnimId(),
                        settings.getPopExitAnimId())
                .replace(fragmentContainerId, newFragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    /**
     * 액티비티 변경
     *
     * @param activity 현재 액티비티
     * @param intent   intent
     */
    public static void replace(@NonNull Activity activity, Intent intent) {
        Objects.requireNonNull(activity, "activity == null");
        Objects.requireNonNull(intent, "intent == null");
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(
                settings.getReplaceEnterAnimId(),
                settings.getReplaceExitAnimId());
    }

    /**
     * 프래그먼트 변경
     *
     * @param currentFragment 현재 프래그먼트
     * @param newFragment     변결할 프래그먼트
     */
    public static void replace(@NonNull Fragment currentFragment, @NonNull Fragment newFragment) {
        Objects.requireNonNull(currentFragment, "currentFragment == null");
        Objects.requireNonNull(newFragment, "newFragment == null");

        FragmentManager fragmentManager = currentFragment.getParentFragmentManager();
        int fragmentContainerId = Objects.requireNonNull(containerIds.get(fragmentManager));
        String tag = getFragmentTag(fragmentContainerId, fragmentManager, newFragment, false);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        settings.getReplaceEnterAnimId(),
                        settings.getReplaceExitAnimId(),
                        settings.getPopEnterAnimId(),
                        settings.getPopExitAnimId())
                .replace(fragmentContainerId, newFragment, tag)
                .commit();
    }

    /**
     * 액티비티 빠져나가기
     *
     * @param activity 현재 액티비티
     */
    public static void pop(@NonNull Activity activity) {
        Objects.requireNonNull(activity, "activity == null");
        activity.finish();
    }

    /**
     * 프래그먼트 빠져나가기
     *
     * @param currentFragment 현재 프래그먼트
     */
    public static void pop(@NonNull Fragment currentFragment) {
        Objects.requireNonNull(currentFragment, "currentFragment == null");
        currentFragment
                .getParentFragmentManager()
                .popBackStack();
    }

    /**
     * 설정값
     */
    private static class Settings {
        private int enterAnimId = R.anim.anim_default_navigator_enter;
        private int exitAnimId = R.anim.anim_default_navigator_exit;
        private int popEnterAnimId = R.anim.anim_default_navigator_pop_enter;
        private int popExitAnimId = R.anim.anim_default_navigator_pop_exit;
        private int replaceEnterAnimId = R.anim.anim_default_navigator_replace_enter;
        private int replaceExitAnimId = R.anim.anim_default_navigator_replace_exit;

        public int getEnterAnimId() {
            return enterAnimId;
        }

        public Settings setEnterAnimId(int enterAnimId) {
            this.enterAnimId = enterAnimId;
            return this;
        }

        public int getExitAnimId() {
            return exitAnimId;
        }

        public Settings setExitAnimId(int exitAnimId) {
            this.exitAnimId = exitAnimId;
            return this;
        }

        public int getPopEnterAnimId() {
            return popEnterAnimId;
        }

        public Settings setPopEnterAnimId(int popEnterAnimId) {
            this.popEnterAnimId = popEnterAnimId;
            return this;
        }

        public int getPopExitAnimId() {
            return popExitAnimId;
        }

        public Settings setPopExitAnimId(int popExitAnimId) {
            this.popExitAnimId = popExitAnimId;
            return this;
        }

        public int getReplaceEnterAnimId() {
            return replaceEnterAnimId;
        }

        public Settings setReplaceEnterAnimId(int replaceEnterAnimId) {
            this.replaceEnterAnimId = replaceEnterAnimId;
            return this;
        }

        public int getReplaceExitAnimId() {
            return replaceExitAnimId;
        }

        public Settings setReplaceExitAnimId(int replaceExitAnimId) {
            this.replaceExitAnimId = replaceExitAnimId;
            return this;
        }
    }
}

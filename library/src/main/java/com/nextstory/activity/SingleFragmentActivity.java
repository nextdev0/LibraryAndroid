package com.nextstory.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nextstory.R;
import com.nextstory.databinding.ActivitySingleFragmentBinding;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 단일 프래그먼트 액티비티
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 * @deprecated 미사용 (사용금지)
 */
@Deprecated
@SuppressWarnings({"unchecked", "UnusedDeclaration"})
public final class SingleFragmentActivity extends BaseActivity<ActivitySingleFragmentBinding> {
    private static final Map<Integer, Consumer<Fragment>> listeners = new LinkedHashMap<>();
    private static final AtomicInteger index = new AtomicInteger(0);
    private static final String EXTRA_FRAGMENT = "extra_fragment_class";
    private static final String EXTRA_INDEX = "extra_index";
    private static final String EXTRA_ORIENTATION = "extra_orientation";
    private Fragment fragment = null;
    private int currentIndex = -1;

    @Deprecated
    public static <T extends Fragment> Intent unspecified(@NonNull Context context,
                                                          @NonNull Class<T> klass) {
        return newIntent(context, klass, null, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Deprecated
    public static <T extends Fragment> Intent unspecified(@NonNull Context context,
                                                          @NonNull Class<T> klass,
                                                          @Nullable Consumer<T> listener) {
        return newIntent(context, klass, listener, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Deprecated
    public static <T extends Fragment> Intent portrait(@NonNull Context context,
                                                       @NonNull Class<T> klass) {
        return newIntent(context, klass, null, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Deprecated
    public static <T extends Fragment> Intent portrait(@NonNull Context context,
                                                       @NonNull Class<T> klass,
                                                       @Nullable Consumer<T> listener) {
        return newIntent(context, klass, listener, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Deprecated
    public static <T extends Fragment> Intent landscape(@NonNull Context context,
                                                        @NonNull Class<T> klass) {
        return newIntent(context, klass, null, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Deprecated
    public static <T extends Fragment> Intent landscape(@NonNull Context context,
                                                        @NonNull Class<T> klass,
                                                        @Nullable Consumer<T> listener) {
        return newIntent(context, klass, listener, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Fragment> Intent newIntent(@NonNull Context context,
                                                         @NonNull Class<T> klass,
                                                         @Nullable Consumer<T> listener,
                                                         int orientation) {
        int index = SingleFragmentActivity.index.getAndIncrement();
        listeners.put(index, (Consumer<Fragment>) listener);
        return new Intent(context, SingleFragmentActivity.class)
                .putExtra(EXTRA_ORIENTATION, orientation)
                .putExtra(EXTRA_FRAGMENT, klass)
                .putExtra(EXTRA_INDEX, index);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getIntent().getIntExtra(
                EXTRA_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setRequestedOrientation(orientation);

        currentIndex = getIntent().getIntExtra(EXTRA_INDEX, -1);

        try {
            Class<? super Fragment> klass =
                    (Class<? super Fragment>) getIntent().getSerializableExtra(EXTRA_FRAGMENT);
            fragment = (Fragment) klass.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitNowAllowingStateLoss();
            Consumer<Fragment> fragmentConsumer = listeners.get(currentIndex);
            if (fragmentConsumer != null) {
                fragmentConsumer.accept(fragment);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        listeners.remove(currentIndex);
        fragment = null;
        super.onDestroy();
    }
}

package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import com.nextstory.activity.BaseActivity;
import com.nextstory.activity.SingleFragmentActivity;
import com.nextstory.field.NonNullLiveData;
import com.nextstory.field.OnDestroyDisposables;
import com.nextstory.sample.databinding.ActivityMainBinding;
import com.nextstory.sample.ui.fragment.TestFragment;

import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("deprecation")
@AndroidEntryPoint
public final class MainActivity extends BaseActivity<ActivityMainBinding> {
    public final NonNullLiveData<Boolean> progress = new NonNullLiveData<>(false);
    private final OnDestroyDisposables disposables = new OnDestroyDisposables(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTransparentTheme();
        applyLightStatusBar(true);

        getBinding().setActivity(this);
        getBinding().setLifecycleOwner(this);

        progress.setValue(true);
        disposables.add(Completable.timer(3000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> progress.setValue(false)));
    }

    public void test1() {
        startActivity(SingleFragmentActivity.portrait(this, TestFragment.class));
    }

    public void test2() {
        startActivity(SingleFragmentActivity.portrait(this, TestFragment.class));
    }
}

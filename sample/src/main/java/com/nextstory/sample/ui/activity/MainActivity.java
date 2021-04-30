package com.nextstory.sample.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.nextstory.app.BaseActivity;
import com.nextstory.sample.databinding.ActivityMainBinding;
import com.nextstory.sample.ui.popup.TestPopup;
import com.nextstory.util.OnDestroyDisposables;
import com.nextstory.util.RxImagePicker;
import com.nextstory.util.RxPermission;

import java.util.Arrays;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
public final class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerSupportedLocales(Arrays.asList(
                Locale.KOREAN,
                Locale.ENGLISH,
                Locale.SIMPLIFIED_CHINESE));

        applyTransparentTheme();
        applyLightStatusBar(true);

        getBinding().setActivity(this);
        getBinding().setLifecycleOwner(this);
    }

    public void test1() {
        startActivity(new Intent(this, Test1Activity.class));
    }

    public void test2() {
        startActivity(new Intent(this, Test2Activity.class));
    }

    public void test3() {
        startActivity(new IntentBuilderTestActivityIntentBuilder(this)
                .setMessage("test")
                .build());
    }

    public void test4(View v) {
        new TestPopup(this)
                .show(v);
    }

    public void test5(View v) {
        OnDestroyDisposables destroyDisposables = new OnDestroyDisposables(this);
        destroyDisposables.add(new RxPermission(this)
                .add(Manifest.permission.READ_EXTERNAL_STORAGE)
                .add(Manifest.permission.CAMERA)
                .request()
                .map(permissionResult -> {
                    if (permissionResult.isPermissionAllGranted()) {
                        return new RxImagePicker(this)
                                .single()
                                .blockingGet();
                    }
                    return Uri.EMPTY;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(permissionResult -> {
                    // no-op
                }));
    }
}

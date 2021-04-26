package com.nextstory.sample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nextstory.app.BaseActivity;
import com.nextstory.sample.databinding.ActivityMainBinding;
import com.nextstory.sample.ui.popup.TestPopup;

import java.util.Arrays;
import java.util.Locale;

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
}

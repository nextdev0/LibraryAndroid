package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import com.nextstory.activity.BaseActivity;
import com.nextstory.field.NonNullLiveData;
import com.nextstory.field.SafeData;
import com.nextstory.sample.databinding.ActivityMainBinding;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author troy
 * @since 1.0
 */
@AndroidEntryPoint
public final class MainActivity extends BaseActivity<ActivityMainBinding> {
    public final NonNullLiveData<String> safeValue = new NonNullLiveData<>("first", true);
    public final NonNullLiveData<String> unsafeValue = new NonNullLiveData<>("first");
    private final SafeData<Boolean> localeToggle = new SafeData<>(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTransparentTheme();
        applyLightStatusBar(true);

        getBinding().setActivity(this);
        getBinding().setLifecycleOwner(this);
    }

    public void onDialogTestClick() {
        new TestDialog().show(this);
    }

    public void onDialogTest2Click() {
        new Test2Dialog().show(this);
    }

    public void onLocaleTestClick() {
        if (localeToggle.getValue()) {
            applyLocale(Locale.KOREAN);
        } else {
            applyLocale(Locale.ENGLISH);
        }
        localeToggle.setValue(!localeToggle.getValue());
        recreate();
    }

    public void onSafeFieldTestClick() {
        safeValue.setValue("second");
        unsafeValue.setValue("second");
    }
}

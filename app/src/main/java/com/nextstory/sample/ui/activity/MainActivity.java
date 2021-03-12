package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import com.nextstory.activity.BaseActivity;
import com.nextstory.di.Inject;
import com.nextstory.field.NonNullLiveData;
import com.nextstory.sample.databinding.ActivityMainBinding;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

/**
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class MainActivity extends BaseActivity<ActivityMainBinding> {
    public final NonNullLiveData<String> safeValue = new NonNullLiveData<>("first");
    public final NonNullLiveData<String> unsafeValue = new NonNullLiveData<>("first", true);

    @Inject
    TestDialog testDialog;
    @Inject
    Test2Dialog test2Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBinding().setActivity(this);
        getBinding().setLifecycleOwner(this);
    }

    public void onInjectTestClick() {
        testDialog.show();
    }

    public void onInjectTest2Click() {
        test2Dialog.show();
    }

    public void onSafeFieldTestClick() {
        safeValue.setValue("second");
        unsafeValue.setValue("second");
    }
}

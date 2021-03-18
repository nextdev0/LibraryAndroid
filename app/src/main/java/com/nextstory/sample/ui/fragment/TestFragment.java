package com.nextstory.sample.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.field.NonNullLiveData;
import com.nextstory.fragment.BaseFragment;
import com.nextstory.sample.databinding.FragmentTestBinding;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@AndroidEntryPoint
public final class TestFragment extends BaseFragment<FragmentTestBinding> {
    public final NonNullLiveData<String> safeValue = new NonNullLiveData<>("first", true);
    public final NonNullLiveData<String> unsafeValue = new NonNullLiveData<>("first");

    @Inject
    TestDialog testDialog;
    @Inject
    Test2Dialog test2Dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setFragment(this);
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

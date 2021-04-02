package com.nextstory.sample.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.field.ListLiveData;
import com.nextstory.field.NonNullLiveData;
import com.nextstory.field.OnDestroyDisposables;
import com.nextstory.fragment.BaseFragment;
import com.nextstory.sample.databinding.FragmentTestBinding;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
@AndroidEntryPoint
public final class TestFragment extends BaseFragment<FragmentTestBinding> {
    public final ListLiveData<String> testList = new ListLiveData<>();
    public final NonNullLiveData<String> safeValue = new NonNullLiveData<>("first", true);
    public final NonNullLiveData<String> unsafeValue = new NonNullLiveData<>("first");
    private final OnDestroyDisposables disposables = new OnDestroyDisposables(this);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setFragment(this);
        getBinding().setLifecycleOwner(this);

        Random random = new Random();
        disposables.add(Observable.timer(1000L, TimeUnit.MILLISECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> testList.add("test_" + random.nextInt(0xffff))));
    }

    public void onDialogTestClick() {
        new TestDialog().show(this);
    }

    public void onDialogTest2Click() {
        new Test2Dialog().show(this);
    }

    public void onSafeFieldTestClick() {
        safeValue.setValue("second");
        unsafeValue.setValue("second");
    }
}

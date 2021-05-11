package com.nextstory.sample.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.annotations.FragmentArgument;
import com.nextstory.annotations.FragmentArgumentsBuilder;
import com.nextstory.app.BaseFragment;
import com.nextstory.app.Navigator;
import com.nextstory.field.ListLiveData;
import com.nextstory.field.NonNullLiveData;
import com.nextstory.sample.databinding.FragmentTestBinding;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;
import com.nextstory.util.OnDestroyDisposables;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
@FragmentArgumentsBuilder
public final class Test1Fragment extends BaseFragment<FragmentTestBinding> {
    public final ListLiveData<String> testList = new ListLiveData<>();
    public final NonNullLiveData<String> safeValue = new NonNullLiveData<>("first", true);
    public final NonNullLiveData<String> unsafeValue = new NonNullLiveData<>("first");
    private final OnDestroyDisposables disposables = new OnDestroyDisposables(this);

    @FragmentArgument
    String arg1 = "test";
    @FragmentArgument
    String arg2 = "empty";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showToast(arg1);
        showToast(arg2);

        getBinding().setFragment(this);
        getBinding().setLifecycleOwner(this);

        Random random = new Random();
        disposables.add(Observable.timer(1000L, TimeUnit.MILLISECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> testList.add("test_" + random.nextInt(0xffff))));
    }

    public void argumentsTest() {
        Navigator.push(this, new Test1FragmentArgumentsBuilder()
                .setArg1("arguments set!")
                .build());
    }

    public void onDialogTestClick() {
        new TestDialog().show(this);
    }

    public void onDialogTest2Click() {
        new Test2Dialog().show(this);
    }

    public void onLocaleTestClick(int index) {
        Locale[] locales = new Locale[]{
                Locale.KOREAN,
                Locale.ENGLISH,
                Locale.CHINESE
        };
        applyLocale(locales[index]);
        requireActivity().recreate();
    }

    public void onSafeFieldTestClick() {
        safeValue.setValue("second");
        unsafeValue.setValue("second");
    }
}

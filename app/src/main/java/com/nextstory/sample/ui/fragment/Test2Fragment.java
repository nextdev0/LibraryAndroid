package com.nextstory.sample.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextstory.field.ListLiveData;
import com.nextstory.field.OnDestroyDisposables;
import com.nextstory.fragment.BaseFragment;
import com.nextstory.sample.databinding.FragmentTest2Binding;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
public final class Test2Fragment extends BaseFragment<FragmentTest2Binding> {
    public final ListLiveData<String> testList = new ListLiveData<>();
    private final OnDestroyDisposables disposables = new OnDestroyDisposables(this);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setFragment(this);
        getBinding().setLifecycleOwner(this);

        Random random = new Random();
        disposables.add(Observable.timer(500L, TimeUnit.MILLISECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> testList.add("test_" + random.nextInt(0xffff))));
    }
}

package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nextstory.app.BaseActivity;
import com.nextstory.field.ListLiveData;
import com.nextstory.sample.databinding.ActivityTest2Binding;
import com.nextstory.util.OnDestroyDisposables;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author troy
 * @since 1.0
 */
public final class Test2Activity extends BaseActivity<ActivityTest2Binding> {
    public final ListLiveData<String> testList = new ListLiveData<>();
    private final OnDestroyDisposables disposables = new OnDestroyDisposables(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBinding().setActivity(this);
        getBinding().setLifecycleOwner(this);

        Random random = new Random();
        disposables.add(Observable.timer(500L, TimeUnit.MILLISECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> testList.add("test_" + random.nextInt(0xffff))));
    }
}

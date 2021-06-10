package com.nextstory.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * {@link CompositeDisposable}
 * (수명주기가 onDestroy일 경우 해제)
 *
 * @author troy
 * @since 1.1
 * @deprecated {@link Disposables#onDestroy(LifecycleOwner)} 사용
 */
@Deprecated
@SuppressWarnings("UnusedDeclaration")
public final class OnDestroyDisposables implements Disposables {
    private final Disposables disposables;

    public OnDestroyDisposables(@NonNull LifecycleOwner lifecycleOwner) {
        disposables = Disposables.onDestroy(lifecycleOwner);
    }

    @Override
    public void clear() {
        disposables.clear();
    }

    @Override
    public void dispose() {
        disposables.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposables.isDisposed();
    }

    @Override
    public boolean add(Disposable d) {
        return disposables.add(d);
    }

    @Override
    public boolean remove(Disposable d) {
        return disposables.remove(d);
    }

    @Override
    public boolean delete(Disposable d) {
        return disposables.delete(d);
    }
}

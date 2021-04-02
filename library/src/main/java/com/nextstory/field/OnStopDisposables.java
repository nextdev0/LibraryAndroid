package com.nextstory.field;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * {@link CompositeDisposable}
 * (수명주기가 onStop일 경우 해제)
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public final class OnStopDisposables {
    private final CompositeDisposable compositeDisposable;

    public OnStopDisposables(@NonNull LifecycleOwner lifecycleOwner) {
        this.compositeDisposable = new CompositeDisposable();
        lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            void onStop() {
                clear();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void onDestroy() {
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        });
    }

    /**
     * @see CompositeDisposable#dispose()
     */
    public void dispose() {
        compositeDisposable.dispose();
    }

    /**
     * @see CompositeDisposable#isDisposed()
     */
    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }

    /**
     * @see CompositeDisposable#add(Disposable)
     */
    public boolean add(@NonNull Disposable disposable) {
        return compositeDisposable.add(disposable);
    }

    /**
     * @see CompositeDisposable#addAll(Disposable...)
     */
    public boolean addAll(@NonNull Disposable... disposables) {
        return compositeDisposable.addAll(disposables);
    }

    /**
     * @see CompositeDisposable#remove(Disposable)
     */
    public boolean remove(@NonNull Disposable disposable) {
        return compositeDisposable.remove(disposable);
    }

    /**
     * @see CompositeDisposable#delete(Disposable)
     */
    public boolean delete(@NonNull Disposable disposable) {
        return compositeDisposable.delete(disposable);
    }

    /**
     * @see CompositeDisposable#clear()
     */
    public void clear() {
        compositeDisposable.clear();
    }

    /**
     * @see CompositeDisposable#size()
     */
    public int size() {
        return compositeDisposable.size();
    }
}

package com.nextstory.field;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nextstory.util.LifecycleCallbacks;
import com.nextstory.util.SimpleActivityLifecycleCallbacks;
import com.nextstory.util.SimpleFragmentLifecycleCallbacks;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * {@link CompositeDisposable}
 * (수명주기가 onStop일 경우 해제)
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public final class OnStopDisposables {
    private final CompositeDisposable compositeDisposable;

    public OnStopDisposables(@NonNull Activity activity) {
        this.compositeDisposable = new CompositeDisposable();
        LifecycleCallbacks
                .registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityStopped(@NonNull Activity activity2) {
                        if (activity == activity2) {
                            clear();
                        }
                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity2) {
                        if (activity == activity2) {
                            LifecycleCallbacks.unregisterActivityLifecycleCallbacks(this);
                        }
                    }
                });
    }

    public OnStopDisposables(@NonNull Fragment fragment) {
        this.compositeDisposable = new CompositeDisposable();
        LifecycleCallbacks
                .registerFragmentLifecycleCallbacks(
                        new SimpleFragmentLifecycleCallbacks() {
                            @Override
                            public void onFragmentStopped(@NonNull FragmentManager fm,
                                                          @NonNull Fragment f) {
                                if (fragment == f) {
                                    clear();
                                }
                            }

                            @Override
                            public void onFragmentViewDestroyed(@NonNull FragmentManager fm,
                                                                @NonNull Fragment f) {
                                if (fragment == f) {
                                    LifecycleCallbacks.unregisterFragmentLifecycleCallbacks(this);
                                }
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

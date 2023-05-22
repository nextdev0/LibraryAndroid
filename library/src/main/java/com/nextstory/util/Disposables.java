package com.nextstory.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.disposables.DisposableContainer;

/**
 * {@link Disposable} 컨테이너 인터페이스
 *
 * @author troy
 * @see CompositeDisposable
 * @since 1.6
 */
@SuppressWarnings("UnusedDeclaration")
public interface Disposables extends Disposable, DisposableContainer {
  /**
   * 일반 {@link Disposables}
   * 수동으로 정리할 때 사용.
   *
   * @return {@link Disposables}
   */
  static Disposables common() {
    return new Disposables() {
      private final CompositeDisposable compositeDisposable = new CompositeDisposable();

      @Override
      public void dispose() {
        compositeDisposable.dispose();
      }

      @Override
      public boolean isDisposed() {
        return compositeDisposable.isDisposed();
      }

      @Override
      public boolean add(Disposable d) {
        return compositeDisposable.add(d);
      }

      @Override
      public boolean remove(Disposable d) {
        return compositeDisposable.remove(d);
      }

      @Override
      public boolean delete(Disposable d) {
        return compositeDisposable.delete(d);
      }

      @Override
      public void clear() {
        compositeDisposable.clear();
      }
    };
  }

  /**
   * 지정한 수명주기에 맞춰서 {@link Disposable}을 정리함.
   *
   * @param lifecycleOwner 수명주기 소유자
   * @param event          발생할 이벤트 시점
   * @return {@link Disposables}
   */
  static Disposables lifecycle(@NonNull LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
    return new Disposables() {
      private final CompositeDisposable compositeDisposable = new CompositeDisposable();

      {
        lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
          @Override
          public void onPause(@NonNull LifecycleOwner owner) {
            clear();
          }

          @Override
          public void onStop(@NonNull LifecycleOwner owner) {
            clear();
          }

          @Override
          public void onDestroy(@NonNull LifecycleOwner owner) {
            clear();
            lifecycleOwner.getLifecycle().removeObserver(this);
          }
        });
      }

      @Override
      public void dispose() {
        compositeDisposable.dispose();
      }

      @Override
      public boolean isDisposed() {
        return compositeDisposable.isDisposed();
      }

      @Override
      public boolean add(Disposable d) {
        return compositeDisposable.add(d);
      }

      @Override
      public boolean remove(Disposable d) {
        return compositeDisposable.remove(d);
      }

      @Override
      public boolean delete(Disposable d) {
        return compositeDisposable.delete(d);
      }

      @Override
      public void clear() {
        compositeDisposable.clear();
      }
    };
  }

  /**
   * 수명주기가 onPause일 경우에 {@link Disposable}을 정리함.
   *
   * @param lifecycleOwner 수명주기 소유자
   * @return {@link Disposables}
   */
  static Disposables onPause(@NonNull LifecycleOwner lifecycleOwner) {
    return lifecycle(lifecycleOwner, Lifecycle.Event.ON_PAUSE);
  }

  /**
   * 수명주기가 onStop일 경우에 {@link Disposable}을 정리함.
   *
   * @param lifecycleOwner 수명주기 소유자
   * @return {@link Disposables}
   */
  static Disposables onStop(@NonNull LifecycleOwner lifecycleOwner) {
    return lifecycle(lifecycleOwner, Lifecycle.Event.ON_STOP);
  }

  /**
   * 수명주기가 onDestroy일 경우에 {@link Disposable}을 정리함.
   *
   * @param lifecycleOwner 수명주기 소유자
   * @return {@link Disposables}
   */
  static Disposables onDestroy(@NonNull LifecycleOwner lifecycleOwner) {
    return lifecycle(lifecycleOwner, Lifecycle.Event.ON_DESTROY);
  }

  /**
   * 한번에 정리
   *
   * @see CompositeDisposable#clear()
   */
  void clear();
}

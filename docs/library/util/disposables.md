# Disposables

## 메서드 목록

- common();
- lifecycle(LifecycleOwner, Lifecycle.Event);
- onPause(LifecycleOwner);
- onStop(LifecycleOwner);
- onDestroy(LifecycleOwner);

## 설명

### common

여러 `Disposable`을 한꺼번에 정리 할 수 있는 컨테이너.

```java
Disposables disposables = Disposables.common();
disposables.add(Observable.just("aaaaa").subscribe());
disposables.add(Observable.just(1).subscribe());
disposables.clear();
```

### lifecycle

지정한 수명주기가 트리거되면 여러 `Disposable`을 한꺼번에 정리 할 수 있는 컨테이너.

```java
Disposables disposables = Disposables.lifecycle(this /* LifecycleOwner */, ifecycle.Event.ON_DESTROY);
disposables.add(Observable.just("aaaaa").subscribe());
disposables.add(Observable.just(1).subscribe());
```

### onStop

수명주기 onStop가 트리거되면 여러 `Disposable`을 한꺼번에 정리 할 수 있는 컨테이너.

```java
Disposables disposables = Disposables.onStop(this /* LifecycleOwner */);
disposables.add(Observable.just("aaaaa").subscribe());
disposables.add(Observable.just(1).subscribe());
```

### onPause

수명주기 onPause가 트리거되면 여러 `Disposable`을 한꺼번에 정리 할 수 있는 컨테이너.

```java
Disposables disposables = Disposables.onPause(this /* LifecycleOwner */);
disposables.add(Observable.just("aaaaa").subscribe());
disposables.add(Observable.just(1).subscribe());
```

### onDestroy

수명주기 onDestroy가 트리거되면 여러 `Disposable`을 한꺼번에 정리 할 수 있는 컨테이너.

```java
Disposables disposables = Disposables.onDestroy(this /* LifecycleOwner */);
disposables.add(Observable.just("aaaaa").subscribe());
disposables.add(Observable.just(1).subscribe());
```

package com.nextstory.field;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link LiveData} 컨테이너
 * <p>
 * 추가한 여러 {@link LiveData}들을 관찰함.
 * <p>
 * 아래와 같이 초기 결과값을 지정한 뒤
 * 여러 {@link LiveData}의 조건식을 넣어 결과를 도출하는 형식으로 사용
 * <pre>
 *     CompositeLiveData&#60;Boolean&#62; enabled = new CompositeLiveData&#60;&#62;(true)
 *             .add(name, (isEnabled, data) -> isEnabled && !data.trim().isEmpty())
 *             .add(email, (isEnabled, data) -> isEnabled && !data.trim().isEmpty())
 *             .add(password, (isEnabled, data) -> isEnabled && !data.trim().isEmpty());
 * </pre>
 *
 * @author troy
 * @since 1.4
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class CompositeLiveData<T> extends MediatorLiveData<T> {
  private final Map<LiveData<?>, CompositeLiveDataFunction<T, T, Object>> functions
    = new LinkedHashMap<>();
  private final T initializeValue;

  public CompositeLiveData(T initializeValue) {
    this.initializeValue = initializeValue;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @Override
  public final <S> void addSource(@NonNull LiveData<S> source,
                                  @NonNull Observer<? super S> onChanged) {
    throw new IllegalStateException("not supported.");
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @Override
  public final <S> void removeSource(@NonNull LiveData<S> toRemote) {
    throw new IllegalStateException("not supported.");
  }

  public <A> CompositeLiveData<T> add(@NonNull LiveData<A> liveData,
                                      @NonNull CompositeLiveDataFunction<T, T, A> function) {
    Objects.requireNonNull(liveData);
    Objects.requireNonNull(function);
    functions.put(liveData, (CompositeLiveDataFunction<T, T, Object>) function);
    super.addSource(liveData, a -> onFilterDataChanged());
    return this;
  }

  public <A> CompositeLiveData<T> remove(@NonNull LiveData<A> liveData) {
    Objects.requireNonNull(liveData);
    functions.remove(liveData);
    super.removeSource(liveData);
    return this;
  }

  private void onFilterDataChanged() {
    T currentValue = initializeValue;
    for (LiveData<?> liveData : functions.keySet()) {
      Object value = liveData.getValue();
      CompositeLiveDataFunction<T, T, Object> function = functions.get(liveData);
      currentValue = Objects.requireNonNull(function)
        .apply(currentValue, value);
    }
    setValue(currentValue);
  }
}

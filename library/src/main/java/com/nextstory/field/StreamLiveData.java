package com.nextstory.field;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 여러 {@link LiveData}의 변경을 관찰하기 위한 클래스
 *
 * @author troy
 * @since 1.2
 * @deprecated {@link CompositeLiveData} 사용
 */
@Deprecated
@SuppressWarnings({"UnusedDeclaration", "unchecked","deprecation"})
public final class StreamLiveData<T> extends MediatorLiveData<T> {
    private final Map<LiveData<?>, StreamLiveDataFunction<T, T, Object>> functions
            = new LinkedHashMap<>();
    private final T initializeValue;

    public StreamLiveData(T initializeValue) {
        this.initializeValue = initializeValue;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @VisibleForTesting
    @Override
    public <S> void addSource(@NonNull LiveData<S> source, @NonNull Observer<? super S> onChanged) {
        super.addSource(source, onChanged);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @VisibleForTesting
    @Override
    public <S> void removeSource(@NonNull LiveData<S> toRemote) {
        super.removeSource(toRemote);
    }

    public <A> StreamLiveData<T> map(LiveData<A> liveData,
                                     StreamLiveDataFunction<T, T, A> function) {
        functions.put(liveData, (StreamLiveDataFunction<T, T, Object>) function);
        addSource(liveData, a -> onCombinedChanged());
        return this;
    }

    private void onCombinedChanged() {
        T currentValue = initializeValue;
        for (LiveData<?> liveData : functions.keySet()) {
            Object value = liveData.getValue();
            StreamLiveDataFunction<T, T, Object> function = functions.get(liveData);
            currentValue = Objects.requireNonNull(function)
                    .apply(currentValue, value);
        }
        setValue(currentValue);
    }
}

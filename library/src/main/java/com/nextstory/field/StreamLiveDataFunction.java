package com.nextstory.field;

/**
 * {@link StreamLiveData} 전용 함수 인터페이스
 *
 * @author troy
 * @since 1.2
 * @deprecated {@link CompositeLiveDataFunction} 사용
 */
@Deprecated
@SuppressWarnings({"UnusedDeclaration", "deprecation", "DeprecatedIsStillUsed"})
public interface StreamLiveDataFunction<R, A1, A2> {
    R apply(A1 arg1, A2 arg2);
}

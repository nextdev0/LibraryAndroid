package com.nextstory.field;

/**
 * {@link StreamLiveData} 전용 함수 인터페이스
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public interface StreamLiveDataFunction<R, A1, A2> {
    R apply(A1 arg1, A2 arg2);
}

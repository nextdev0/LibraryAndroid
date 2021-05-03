package com.nextstory.field;

/**
 * {@link CompositeLiveData} 필더 함수 인터페이스
 *
 * @author troy
 * @since 1.4
 */
@SuppressWarnings("UnusedDeclaration")
public interface CompositeLiveDataFunction<R, A1, A2> {
    R apply(A1 currentValue, A2 data);
}

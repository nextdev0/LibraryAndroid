package com.nextstory.http;

import androidx.annotation.NonNull;

/**
 * 응답 결과 변환 인터페이스
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public interface ResponseConverter {
    <T> T convert(@NonNull String response, @NonNull Class<T> clazz);
}

package com.nextstory.util.json;

import java.lang.reflect.Type;

/**
 * JSON 서비스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public interface JsonService {
    /**
     * json 직렬화
     *
     * @param source 변환할 인스턴스
     * @return json 소스
     */
    String serialize(Object source);

    /**
     * json 역직렬화
     *
     * @param jsonSource json 소스
     * @param typeClass  변환 모델 클래스
     * @param <T>        반환 타입
     * @return 반환 인스턴스
     */
    <T> T deserialize(String jsonSource, Class<T> typeClass);

    /**
     * json 역직렬화
     *
     * @param jsonSource json 소스
     * @param type       변환 모델 타입
     * @param <T>        반환 타입
     * @return 반환 인스턴스
     */
    <T> T deserialize(String jsonSource, Type type);
}

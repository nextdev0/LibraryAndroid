package com.nextstory.di;

import java.util.List;

/**
 * 의존성 모듈 컨테이너 인터페이스
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public interface ModuleContainer {
    /**
     * 모든 의존성 모듈 반환
     *
     * @return 모듈 목록
     */
    List<Module> getModules();
}

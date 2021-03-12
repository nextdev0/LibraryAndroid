package com.nextstory.util.permission;

import java.util.List;

/**
 * 권한 요청 리스너
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public interface PermissionListener {
    /**
     * 권한 요청 결과
     *
     * @param deniedPermissions 거부된 권한 목록
     */
    void onPermissionResult(List<String> deniedPermissions);
}

package com.nextstory.widget.util.blur;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.RestrictTo;

/**
 * 블러 구현 인터페이스
 *
 * @author troy
 * @since 1.4
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface Blur {
    /**
     * 블러 이미지 준비
     *
     * @param context 컨텍스트
     * @param buffer  블러 이미지 저장용 비트맵
     * @param radius  블러 값
     * @return 성공 유무
     */
    boolean prepare(Context context, Bitmap buffer, float radius);

    /**
     * 블러 적용
     *
     * @param input  입력
     * @param output 출력
     */
    void blur(Bitmap input, Bitmap output);

    /**
     * 해제
     */
    void release();
}

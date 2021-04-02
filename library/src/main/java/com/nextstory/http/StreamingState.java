package com.nextstory.http;

import androidx.annotation.RestrictTo;

/**
 * 스트리밍 상태 모델
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public final class StreamingState {
    private final byte[] currentBuffer;
    private long currentIndex;
    private long length;

    public StreamingState() {
        this(0L, 1L, 1024);
    }

    public StreamingState(long currentIndex, long length, int bufferSize) {
        this.currentIndex = currentIndex;
        this.length = length;
        this.currentBuffer = new byte[bufferSize];
    }

    /**
     * @return 현재 다운로드된 데이터 버퍼
     */
    public byte[] getCurrentBuffer() {
        return currentBuffer;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setCurrentBuffer(byte[] buffer) {
        System.arraycopy(buffer, 0, currentBuffer, 0, buffer.length);
    }

    /**
     * @return 현재 다운로드 위치
     */
    public long getCurrentIndex() {
        return currentIndex;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setCurrentIndex(long currentIndex) {
        this.currentIndex = currentIndex;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void addCurrentIndex(long length) {
        this.currentIndex += length;
    }

    /**
     * @return 응답 데이터 크기
     */
    public long getLength() {
        return length;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setLength(long length) {
        this.length = length;
    }

    /**
     * @return 진행 상태, 0~1 사이의 실수 (백분율)
     */
    public float getProgress() {
        return (float) currentIndex / length;
    }
}

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

    public byte[] getCurrentBuffer() {
        return currentBuffer;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setCurrentBuffer(byte[] buffer) {
        System.arraycopy(buffer, 0, currentBuffer, 0, buffer.length);
    }

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

    public long getLength() {
        return length;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void setLength(long length) {
        this.length = length;
    }

    public float getProgress() {
        return (float) currentIndex / length;
    }
}

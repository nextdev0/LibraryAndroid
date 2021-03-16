package com.nextstory.http;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * GET 요청 빌더
 *
 * @author troy
 * @version 1.1
 * @since 1.1
 */
@SuppressWarnings("unchecked")
final class GetHttpRequestBuilder implements HttpRequestBuilder {
    private final StringBuilder fields = new StringBuilder();
    private final HttpClient httpClient;
    private String url = "";

    GetHttpRequestBuilder(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public HttpRequestBuilder addField(String key, Object value) {
        this.fields.append(this.fields.length() == 0 ? '?' : '&');
        this.fields.append(key);
        this.fields.append('=');
        if (value instanceof String) {
            try {
                this.fields.append(URLEncoder.encode((String) value, "utf-8"));
            } catch (UnsupportedEncodingException ignore) {
                this.fields.append(value);
            }
        } else {
            this.fields.append(value);
        }
        return this;
    }

    @Override
    public HttpRequestBuilder addMultipartByUri(@NonNull String body, @NonNull Uri uri) {
        throw new UnsupportedOperationException("Multipart request is not supported.");
    }

    @Override
    public <T> Single<T> request(Type type) {
        return Single.create(e -> {
            String response = internalRequest();
            if (!(type instanceof Class<?>)) {
                e.onError(new IllegalStateException());
                return;
            }
            Class<T> clazz = (Class<T>) type;
            if (clazz.isAssignableFrom(String.class)) {
                e.onSuccess((T) response);
                return;
            }
            T convert = httpClient.onResponse(response, clazz);
            if (convert == null) {
                e.onError(new IllegalStateException(
                        "Must be registered HttpClient ResponseConverter"));
                return;
            }
            e.onSuccess(convert);
        });
    }

    @Override
    public Observable<StreamingState> requestStreaming(int bufferSize) {
        return Observable.create(e -> {
            String url = this.httpClient.getBaseUrl() + this.url + this.fields.toString();

            // 초기 상태 알림
            StreamingState state = new StreamingState(0, 1, bufferSize);
            e.onNext(state);

            // 연결
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "*/*");
            httpConnection.setRequestProperty("Accept-Encoding", "");
            httpConnection.connect();

            // 다운로드 길이 반환
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                state.setLength(httpConnection.getContentLengthLong());
            } else {
                String contentLength = httpConnection.getHeaderField("Content-Length");
                state.setLength(Long.parseLong(contentLength));
            }

            // 다운로드
            InputStream inputStream = httpConnection.getInputStream();
            int read;
            while ((read = inputStream.read(state.getCurrentBuffer())) != -1) {
                state.addCurrentIndex(read);
                e.onNext(state);
            }
            inputStream.close();
            httpConnection.disconnect();

            e.onComplete();
        });
    }

    private String internalRequest() {
        try {
            String url = this.httpClient.getBaseUrl() + this.url + this.fields.toString();

            // 연결
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setDefaultUseCaches(true);
            httpConnection.setReadTimeout(httpClient.getReadTimeout());
            httpConnection.setConnectTimeout(httpClient.getConnectionTimeout());
            httpConnection.setRequestMethod("GET");

            // 필드 전송
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    httpConnection.getOutputStream(), StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.write("");
            printWriter.flush();
            printWriter.close();

            // 응답
            InputStreamReader inputStreamReader = new InputStreamReader(
                    httpConnection.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder responseBuilder = new StringBuilder();
            int read;
            while ((read = bufferedReader.read()) != -1) {
                responseBuilder.append((char) read);
            }
            bufferedReader.close();
            httpConnection.disconnect();
            return responseBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

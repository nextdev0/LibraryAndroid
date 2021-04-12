package com.nextstory.http;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * POST 요청 빌더
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
final class PostHttpRequestBuilder implements HttpRequestBuilder {
    private static final String TAG = "HttpRequest";
    private final StringBuilder fields = new StringBuilder();
    private final Map<String, Uri> multipart = new HashMap<>();
    private final HttpClient httpClient;
    private String url = "";

    PostHttpRequestBuilder(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public HttpRequestBuilder addField(String key, Object value) {
        this.fields.append('&');
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
        multipart.put(body, uri);
        return this;
    }

    @Override
    public <T> Single<T> request(Type type) {
        return Single.create(e -> {
            String response = (multipart.size() > 0)
                    ? internalMultipartRequest()
                    : internalUrlEncodedRequest();
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
            String url = this.httpClient.getBaseUrl() + this.url;
            Log.d(TAG, "Request Post " + url);

            // 초기 상태 알림
            StreamingState state = new StreamingState(0, 1, bufferSize);
            e.onNext(state);

            // 연결
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "*/*");
            httpConnection.setRequestProperty("Accept-Encoding", "");
            httpConnection.connect();

            // 필드 전송
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    httpConnection.getOutputStream(), StandardCharsets.UTF_8);
            outputStreamWriter.write(fields.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

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

    private String internalUrlEncodedRequest() {
        try {
            String url = this.httpClient.getBaseUrl() + this.url;
            Log.d(TAG, "Request Post " + url);

            // 연결
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setDefaultUseCaches(true);
            httpConnection.setReadTimeout(httpClient.getReadTimeout());
            httpConnection.setConnectTimeout(httpClient.getConnectionTimeout());
            httpConnection.setRequestMethod("POST");

            // 필드 전송
            Log.d(TAG, "Body " + fields.toString());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    httpConnection.getOutputStream(), StandardCharsets.UTF_8);
            outputStreamWriter.write(fields.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

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

    private String internalMultipartRequest() {
        try {
            String url = this.httpClient.getBaseUrl() + this.url;
            Log.d(TAG, "Request multipart " + url);

            // 연결
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setReadTimeout(httpClient.getReadTimeout());
            httpConnection.setConnectTimeout(httpClient.getConnectionTimeout());
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

            // 전송
            DataOutputStream outputStream = new DataOutputStream(httpConnection.getOutputStream());
            for (String body : multipart.keySet()) {
                outputStream.writeBytes("--*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; ");
                outputStream.writeBytes(body);
                outputStream.writeBytes("\r\n\r\n");
                InputStream inputStream = httpClient.getContext()
                        .getContentResolver()
                        .openInputStream(Objects.requireNonNull(multipart.get(body)));
                if (inputStream == null) {
                    return "";
                }
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                inputStream.close();
            }
            outputStream.writeBytes("\r\n--*****--\r\n");
            outputStream.flush();
            outputStream.close();

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

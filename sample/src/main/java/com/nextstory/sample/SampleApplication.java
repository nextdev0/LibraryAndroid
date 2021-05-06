package com.nextstory.sample;

import android.app.Application;

import com.google.gson.Gson;
import com.nextstory.util.AutoSharedPreferenceUtils;

/**
 * 애플리케이션
 *
 * @author troy
 * @since 1.0
 */
public final class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AutoSharedPreferenceUtils.registerConverter(new AutoSharedPreferenceUtils.Converter() {
            final Gson gson = new Gson();

            @Override
            public String serialize(Object object) {
                return gson.toJson(object);
            }

            @Override
            public <T> T deserialize(String source, Class<T> klass) {
                return gson.fromJson(source, klass);
            }
        });
    }
}

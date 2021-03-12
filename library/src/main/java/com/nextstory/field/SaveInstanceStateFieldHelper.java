package com.nextstory.field;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.startup.Initializer;

import com.nextstory.util.SimpleActivityLifecycleCallbacks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 필드 상태 저장 구현
 * 액티비티 내 {@link SaveInstanceStateField}를 구현한 필드들의 상태를 저장 및 복구함.
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SaveInstanceStateFieldHelper
        implements Initializer<Object>, SimpleActivityLifecycleCallbacks {
    @NonNull
    @Override
    public Object create(@NonNull Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(this);
        return new Object();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity,
                                  @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            List<SaveInstanceStateField> fields = findFields(activity);
            for (SaveInstanceStateField field : fields) {
                field.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity,
                                            @NonNull Bundle outState) {
        List<SaveInstanceStateField> fields = findFields(activity);
        for (SaveInstanceStateField field : fields) {
            field.onSaveInstanceState(outState);
        }
    }

    /**
     * 필드 내 {@link SaveInstanceStateField} 구현 목록를 반환.
     *
     * @param activity 액티비티
     * @return 목록
     */
    private List<SaveInstanceStateField> findFields(Activity activity) {
        List<SaveInstanceStateField> result = new ArrayList<>();
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean isAccessible = field.isAccessible();
            try {
                Object object = field.get(activity);
                if (object instanceof SaveInstanceStateField) {
                    SaveInstanceStateField saveInstanceStateField = (SaveInstanceStateField) object;
                    saveInstanceStateField.setKey(field.getName());
                    result.add(saveInstanceStateField);
                }
            } catch (IllegalAccessException ignore) {
                // no-op
            }
            field.setAccessible(isAccessible);
        }
        return result;
    }
}

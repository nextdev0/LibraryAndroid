package com.nextstory.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.startup.Initializer;

import com.nextstory.util.SimpleActivityLifecycleCallbacks;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 의존성 주입기
 *
 * @author troy
 * @version 1.0.2
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class DependencyInjector
        implements Initializer<Object>, SimpleActivityLifecycleCallbacks {
    private final Map<Class<?>, Object> singletons = new LinkedHashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new LinkedHashMap<>();
    private final List<Module> modules = new ArrayList<>();
    private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentAttached(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment,
                                       @NonNull Context context) {
            for (Module klass : modules) {
                if (klass instanceof AbstractModule) {
                    ((AbstractModule) klass).performFragmentBind(fragment);
                }
            }
            inject(fragment);
            for (Module klass : modules) {
                if (klass instanceof AbstractModule) {
                    ((AbstractModule) klass).performFragmentUnbind();
                }
            }
        }
    };

    @NonNull
    @Override
    public Object create(@NonNull Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(this);

        if (application instanceof ModuleContainer) {
            ModuleContainer moduleContainer = (ModuleContainer) application;
            modules.addAll(Objects.requireNonNull(moduleContainer.getModules()));

            // 정의한 모듈 처리
            for (Module module : modules) {
                if (module instanceof AbstractModule) {
                    AbstractModule abstractModule = (AbstractModule) module;
                    abstractModule.performApplicationBind(application);
                }
                Method[] methods = module.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    Annotation annotation = method.getAnnotation(Provide.class);
                    if (annotation != null) {
                        Class<?> returnType = method.getReturnType();
                        if (providers.containsKey(returnType)) {
                            throw new IllegalStateException(
                                    "type " + returnType.getName() + " is already provided.");
                        }
                        providers.put(returnType, () -> resolve(returnType, module, method));
                    }
                }
            }
        }

        return new Object();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        for (Module klass : modules) {
            if (klass instanceof AbstractModule) {
                ((AbstractModule) klass).performActivityBind(activity);
            }
        }
        inject(activity);
        for (Module klass : modules) {
            if (klass instanceof AbstractModule) {
                ((AbstractModule) klass).performActivityUnbind();
            }
        }
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity.getSupportFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
        }
    }

    /**
     * 의존성 처리
     *
     * @param returnType 반환될 타입
     * @param module     모듈 객체
     * @param method     의존성 제공 메서드
     * @return 의존성 객체
     */
    @NonNull
    private Object resolve(Class<?> returnType, Object module, Method method) {
        if (method.getAnnotation(Singleton.class) != null) {
            if (!singletons.containsKey(returnType)) {
                singletons.put(returnType, resolveInternal(returnType, module, method));
            }
            return Objects.requireNonNull(singletons.get(returnType));
        }
        return resolveInternal(returnType, module, method);
    }

    /**
     * 내부 의존성 처리
     *
     * @param returnType 반환될 타입
     * @param module     모듈 객체
     * @param method     의존성 제공 메서드
     * @return 의존성 객체
     */
    private Object resolveInternal(Class<?> returnType, Object module, Method method) {
        try {
            Class<?>[] argTypes = method.getParameterTypes();
            if (argTypes.length == 0) {
                return method.invoke(module);
            }
            Object[] argInstances = new Object[argTypes.length];
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> argType = argTypes[i];
                if (!providers.containsKey(argType)) {
                    throw new IllegalStateException("cannot " +
                            "type " + returnType.getSimpleName() + " create. " +
                            "must have argument " + argType.getSimpleName() + ".");
                }
                argInstances[i] = providers.get(argType).get();
            }
            return method.invoke(module, argInstances);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
            throw new IllegalStateException("Cannot resolve " +
                    returnType.getCanonicalName() + " type.");
        }
    }

    /**
     * 의존성 반환
     *
     * @return 의존성
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public <T> T get(Class<T> type) {
        return (T) providers.get(type).get();
    }

    /**
     * 의존성 주입
     *
     * @param injectionObject 주입할 객체
     */
    public void inject(@NonNull Object injectionObject) {
        Class<?> klass = injectionObject.getClass();
        for (Field field : klass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Class<?> fieldType = field.getType();
                try {
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(injectionObject, providers.get(fieldType).get());
                    field.setAccessible(isAccessible);
                } catch (Throwable ignore) {
                    throw new IllegalStateException("Not registered dependency "
                            + fieldType.getCanonicalName() + " type.");
                }
            }
        }
    }
}

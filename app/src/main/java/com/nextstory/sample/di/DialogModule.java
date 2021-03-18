package com.nextstory.sample.di;

import android.content.Context;

import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

/**
 * 다이얼로그 의존성 모듈
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@Module
@InstallIn({ActivityComponent.class, FragmentComponent.class})
public final class DialogModule {
    @Provides
    public static TestDialog provideTestDialog(@ActivityContext Context context) {
        return new TestDialog(context);
    }

    @Provides
    public static Test2Dialog provideTest2Dialog(@ActivityContext Context context) {
        return new Test2Dialog(context);
    }
}

package com.nextstory.sample.di;

import com.nextstory.di.AbstractModule;
import com.nextstory.di.Provide;
import com.nextstory.sample.ui.dialog.Test2Dialog;
import com.nextstory.sample.ui.dialog.TestDialog;

/**
 * 다이얼로그 의존성 모듈
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class DialogModule extends AbstractModule {
    @Provide
    public TestDialog provideTestDialog() {
        return new TestDialog(getContext());
    }

    @Provide
    public Test2Dialog provideTest2Dialog() {
        return new Test2Dialog(getContext());
    }
}

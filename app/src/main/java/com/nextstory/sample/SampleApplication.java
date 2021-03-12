package com.nextstory.sample;

import android.app.Application;

import com.nextstory.di.Module;
import com.nextstory.di.ModuleContainer;
import com.nextstory.sample.di.DialogModule;

import java.util.Collections;
import java.util.List;

/**
 * 애플리케이션
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class SampleApplication extends Application implements ModuleContainer {
    @Override
    public List<Module> getModules() {
        // Arrays.asList();
        return Collections.singletonList(
                new DialogModule()
        );
    }
}

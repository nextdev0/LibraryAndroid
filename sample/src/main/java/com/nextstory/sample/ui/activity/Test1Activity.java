package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nextstory.app.BaseActivity;
import com.nextstory.app.WindowController;
import com.nextstory.sample.R;
import com.nextstory.sample.databinding.ActivityTest1Binding;
import com.nextstory.sample.ui.fragment.Test1Fragment;

/**
 * @author troy
 * @since 1.0
 */
public final class Test1Activity extends BaseActivity<ActivityTest1Binding> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowController()
                .applyWindowType(WindowController.TYPE_OVERLAY_SYSTEM_BARS);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Test1Fragment())
                .commit();
    }
}

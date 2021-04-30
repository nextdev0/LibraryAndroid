package com.nextstory.sample.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nextstory.app.BaseActivity;
import com.nextstory.sample.R;
import com.nextstory.sample.databinding.ActivityTest1Binding;
import com.nextstory.sample.ui.fragment.Test1FragmentArgumentsBuilder;

/**
 * @author troy
 * @since 1.0
 */
public final class Test1Activity extends BaseActivity<ActivityTest1Binding> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, new Test1FragmentArgumentsBuilder()
                        .build())
                .addToBackStack(null)
                .commit();
    }
}

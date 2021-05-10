package com.nextstory.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

/**
 * 내부 AR 프래그먼트
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
class LibGdxFragment extends AndroidFragmentApplication {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initializeForView(
                (ApplicationListener) requireActivity(),
                new AndroidApplicationConfiguration());
    }
}

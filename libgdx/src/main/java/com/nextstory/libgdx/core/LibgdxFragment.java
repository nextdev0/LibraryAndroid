package com.nextstory.libgdx.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.nextstory.libgdx.Libgdx;

/**
 * Libgdx 프래그먼트
 *
 * @author troy
 * @since 2.1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LibgdxFragment extends AndroidFragmentApplication {
    private Libgdx libgdx = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FrameLayout libGdxContainer = new FrameLayout(requireContext());

        View libgdxView = initializeForView(libgdx, new AndroidApplicationConfiguration());
        libGdxContainer.addView(libgdxView, new FrameLayout.LayoutParams(-1, -1));

        View view = libgdx.onCreateSurfaceUIView();
        if (view != null) {
            libGdxContainer.addView(view, new FrameLayout.LayoutParams(-1, -1));
        }

        return libGdxContainer;
    }

    @Override
    public void onDestroyView() {
        libgdx = null;
        super.onDestroyView();
    }

    public void setLibgdx(Libgdx libgdx) {
        this.libgdx = libgdx;
    }
}

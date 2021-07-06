package com.nextstory.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

/**
 * 기본 프래그먼트
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBaseFragment extends Fragment {
    private final WindowController windowController = new WindowController(this);
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            onBackPressed();
            setEnabled(true);
        }
    };

    private ResourcesController resourcesController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resourcesController = new ResourcesController(requireContext());

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backPressedCallback);
    }

    @Override
    public void onDestroyView() {
        resourcesController = null;
        super.onDestroyView();
    }

    /**
     * 뒤로가기 동작
     */
    public void onBackPressed() {
        try {
            if (isVisible()) {
                backPressedCallback.setEnabled(false);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        } catch (Exception e) {
            backPressedCallback.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    /**
     * @return 윈도우 설정
     * @since 2.0
     */
    @NonNull
    public final WindowController getWindowController() {
        return windowController;
    }

    /**
     * @return 리소스 설정
     * @since 2.0
     */
    @NonNull
    public final ResourcesController getResourcesController() {
        return resourcesController;
    }

    /**
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.nextstory.app;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nextstory.R;
import com.nextstory.util.Unsafe;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * 기본 바텀시트 다이얼로그 프래그먼트
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public class BaseBottomSheetDialogFragment<B extends ViewDataBinding> extends DialogFragment {
  private BaseBottomSheetDialog<B> dialog = null;
  private WeakReference<Bundle> savedInstanceState = null;
  private ResourcesController resourcesController;

  @Override
  public int getTheme() {
    return R.style.Theme_Dialog_Base_BottomDialog;
  }

  @CallSuper
  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    this.savedInstanceState = new WeakReference<>(savedInstanceState);
    dialog = new BaseBottomSheetDialog<B>(requireContext(), getTheme()) {
    };
    return dialog;
  }

  @CallSuper
  @Override
  public void onStart() {
    if (dialog.binding == null) {
      Class<?> klass = Unsafe.getGenericClass(this, 0);
      if (klass != null) {
        dialog.binding = Unsafe.invoke(klass, "inflate", getLayoutInflater());
      }
    }
    super.onStart();
    onDialogCreated(dialog, savedInstanceState == null ? null : savedInstanceState.get());
  }

  @CallSuper
  public void onDialogCreated(BaseBottomSheetDialog<B> dialog,
                              @Nullable Bundle savedInstanceState) {
    resourcesController = new ResourcesController(requireContext());

    if (savedInstanceState != null) {
      dialog.onCreate(null);
    }
  }

  public final void show(@NonNull FragmentActivity activity) {
    show(activity.getSupportFragmentManager());
  }

  public final void show(@NonNull FragmentManager fragmentManager) {
    super.show(fragmentManager, getClass().getSimpleName());
  }

  public final void show(@NonNull Fragment fragment) {
    show(fragment.getParentFragmentManager());
  }

  public final void show(@NonNull FragmentTransaction fragmentTransaction) {
    super.show(fragmentTransaction, getClass().getSimpleName());
  }

  public void cancel() {
    if (dialog != null) {
      dialog.cancel();
    }
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

  /**
   * @return 뷰 바인딩 인스턴스
   */
  protected B getBinding() {
    return dialog.getBinding();
  }

  /**
   * @return 리소스 설정
   * @since 2.0
   */
  @NonNull
  public final ResourcesController getResourcesController() {
    return Objects.requireNonNull(resourcesController);
  }
}

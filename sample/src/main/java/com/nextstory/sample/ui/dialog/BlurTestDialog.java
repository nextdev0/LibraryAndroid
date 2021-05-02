package com.nextstory.sample.ui.dialog;

import com.nextstory.app.BaseBottomSheetDialogFragment;
import com.nextstory.sample.databinding.DialogBlurTestBinding;

/**
 * @author troy
 * @since 1.0
 */
public final class BlurTestDialog extends BaseBottomSheetDialogFragment<DialogBlurTestBinding> {
    @Override
    protected float getDimAmount() {
        return 0f;
    }
}

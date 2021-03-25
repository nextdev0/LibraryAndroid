package com.nextstory.sample.ui.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nextstory.dialog.BaseBottomSheetDialog;
import com.nextstory.dialog.BaseBottomSheetDialogFragment;
import com.nextstory.sample.databinding.DialogTest2Binding;

/**
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class Test2Dialog extends BaseBottomSheetDialogFragment<DialogTest2Binding> {
    @Override
    public void onDialogCreated(BaseBottomSheetDialog<DialogTest2Binding> dialog,
                                @Nullable Bundle savedInstanceState) {
        super.onDialogCreated(dialog, savedInstanceState);
        getBinding().setDialog(this);
    }
}

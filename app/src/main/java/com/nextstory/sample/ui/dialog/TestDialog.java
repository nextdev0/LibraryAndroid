package com.nextstory.sample.ui.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nextstory.app.BaseDialog;
import com.nextstory.app.BaseDialogFragment;
import com.nextstory.sample.databinding.DialogTestBinding;

/**
 * @author troy
 * @since 1.0
 */
public final class TestDialog extends BaseDialogFragment<DialogTestBinding> {
    @Override
    public void onDialogCreated(BaseDialog<DialogTestBinding> dialog,
                                @Nullable Bundle savedInstanceState) {
        super.onDialogCreated(dialog, savedInstanceState);
        getBinding().setDialog(this);
    }
}

package com.nextstory.sample.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.nextstory.dialog.BaseDialog;
import com.nextstory.sample.databinding.DialogTestBinding;

/**
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class TestDialog extends BaseDialog<DialogTestBinding> {
    public TestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().setDialog(this);
    }
}

package com.nextstory.sample.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.nextstory.dialog.BaseBottomSheetDialog;
import com.nextstory.sample.databinding.DialogTest2Binding;

/**
 * @author troy
 * @version 1.0
 * @since 1.0
 */
public final class Test2Dialog extends BaseBottomSheetDialog<DialogTest2Binding> {
    public Test2Dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().setDialog(this);
    }
}

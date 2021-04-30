package com.nextstory.sample.ui.popup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nextstory.app.BasePopupWindow;
import com.nextstory.sample.databinding.PopupTestBinding;

/**
 * 테스트 팝업창
 *
 * @author troy
 * @since 1.0
 */
public final class TestPopup extends BasePopupWindow<PopupTestBinding> {
    public TestPopup(@NonNull Context context) {
        super(context);
    }
}

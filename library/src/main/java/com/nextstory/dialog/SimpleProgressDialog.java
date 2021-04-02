package com.nextstory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

/**
 * 간단한 프로그래스창
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class SimpleProgressDialog extends Dialog {
    public SimpleProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onBackPressed() {
        // 동작 생략
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 배경 없애기
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 터치 및 닫기 불가능하도록 설정
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // 프로그레스 표시
        LinearLayout background = new LinearLayout(getContext());
        background.setOrientation(LinearLayout.VERTICAL);
        background.setGravity(Gravity.CENTER);
        background.addView(new ProgressBar(getContext()));

        // 뷰 설정
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(background, params);
    }
}

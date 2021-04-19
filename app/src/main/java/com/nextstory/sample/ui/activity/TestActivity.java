package com.nextstory.sample.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextstory.annotations.IntentBuilder;
import com.nextstory.annotations.IntentExtra;

/**
 * @author troy
 * @since 1.0
 */
@IntentBuilder
public final class TestActivity extends AppCompatActivity {
    @IntentExtra
    String message;
    @IntentExtra
    String message2 = "empty";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, message2, Toast.LENGTH_SHORT).show();
    }
}

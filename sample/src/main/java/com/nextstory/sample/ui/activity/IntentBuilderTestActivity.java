package com.nextstory.sample.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextstory.annotations.ActivityIntentBuilder;
import com.nextstory.annotations.ActivityIntentExtra;

/**
 * @author troy
 * @since 1.0
 */
@ActivityIntentBuilder
public final class IntentBuilderTestActivity extends AppCompatActivity {
  @ActivityIntentExtra
  String message;
  @ActivityIntentExtra
  String message2 = "empty";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    Toast.makeText(this, message2, Toast.LENGTH_SHORT).show();
  }
}

package com.nextstory.sample

import android.Manifest
import android.os.Bundle
import com.nextstory.activity.FragmentActivityIntent
import com.nextstory.activity.result.ImagePickerLauncher
import com.nextstory.activity.result.RequestPermissionLauncher
import com.nextstory.app.AbstractActivity
import com.nextstory.app.currentLocale
import com.nextstory.sample.databinding.ActivityMainBinding
import com.nextstory.util.jsonOf
import com.nextstory.util.savedStateJsonOf
import com.nextstory.databinding.viewDataBinding
import com.nextstory.widget.WindowConfig
import java.util.Locale

class MainActivity : AbstractActivity() {
  private val binding by viewDataBinding<ActivityMainBinding>(R.layout.activity_main)

  val test by savedStateJsonOf(
    "background" to 0xffffffff,
    "icon" to true,
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding.activity = this
    binding.lifecycleOwner = this

    setContentView(binding.root)
  }

  val writeStorageTestLauncher = RequestPermissionLauncher(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
  ) {
    val intent = FragmentActivityIntent(this@MainActivity, TestFragment::class.java, jsonOf(123))
    startActivity(intent)
  }

  fun windowConfig(what: String) {
    if (what == "normal") {
      currentLocale = Locale.KOREA
      binding.layoutRoot.type = WindowConfig.Type.Normal
    }

    if (what == "extended") {
      currentLocale = Locale.ENGLISH
      binding.layoutRoot.type = WindowConfig.Type.Extended
    }

    if (what == "fullscreen") {
      currentLocale = Locale.CHINESE
      binding.layoutRoot.type = WindowConfig.Type.Fullscreen
    }
  }

  val imagePickerLauncher = ImagePickerLauncher {

  }
}

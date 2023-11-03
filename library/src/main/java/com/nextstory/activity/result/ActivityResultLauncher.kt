@file:Suppress("unused", "FunctionName", "SpellCheckingInspection")

package com.nextstory.activity.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nextstory.app.mainThreadHandler

/**
 * 액티비티 결과 처리
 *
 * [ActivityResultLauncher]를 간소화한 함수
 *
 * 예시:
 * ```
 * private val launcher = ActivityResultLauncher { result ->
 *   if (result == null) {
 *     // RESULT_CANCEL
 *     return@ActivityResultLauncher
 *   }
 *
 *   // RESULT_OK
 *   val resultValue = result.getStringExtra("result_value")
 *   println(resultValue ?: "empty")
 * }
 *
 * // ...
 *
 * val intent = Intent(this, TestActivity::class.java)
 * launcher.launch(intent)
 * ```
 */
fun LifecycleOwner.ActivityResultLauncher(
  block: (Intent?) -> Unit,
) = DefaultActivityResultLauncher().also {
  ActivityResultImpl(it, ActivityResultContracts.StartActivityForResult(), block)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
fun LifecycleOwner.ActivityResultImpl(
  lazyLauncher: ActivityResultLauncherWrapper,
  contract: ActivityResultContract<Intent, ActivityResult>,
  block: (Intent?) -> Unit,
) {
  if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
    return
  }

  fun resultBlock(result: ActivityResult) {
    if (result.resultCode != Activity.RESULT_OK) {
      block(null)
      return
    }
    block(result.data)
  }

  lifecycle.addObserver(object : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
      if (owner != this@ActivityResultImpl) {
        return
      }

      when (this@ActivityResultImpl) {
        is ComponentActivity -> {
          lazyLauncher.context = this@ActivityResultImpl
          lazyLauncher.baseLauncher = registerForActivityResult(contract) { resultBlock(it) }
        }

        is Fragment -> {
          lazyLauncher.context = this@ActivityResultImpl.requireContext()
          lazyLauncher.baseLauncher = registerForActivityResult(contract) { resultBlock(it) }
        }

        else -> {
          throw UnsupportedOperationException()
        }
      }
    }

    override fun onDestroy(owner: LifecycleOwner) {
      if (owner != this@ActivityResultImpl) {
        return
      }

      lazyLauncher.baseLauncher = null
      lazyLauncher.context = null

      lifecycle.removeObserver(this)
    }
  })
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
abstract class ActivityResultLauncherWrapper {
  @RestrictTo(RestrictTo.Scope.LIBRARY)
  internal var baseLauncher: ActivityResultLauncher<Intent>? = null

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  internal var context: Context? = null
}

class DefaultActivityResultLauncher : ActivityResultLauncherWrapper() {
  fun launch(input: Intent?) {
    mainThreadHandler.post {
      baseLauncher!!.launch(input)
    }
  }

  fun launch(input: Intent?, options: ActivityOptionsCompat?) {
    mainThreadHandler.post {
      baseLauncher!!.launch(input, options)
    }
  }
}

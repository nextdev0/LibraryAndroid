package com.nextstory.activity.result

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LifecycleOwner
import com.nextstory.app.mainThreadHandler

/**
 * 권한 요청
 *
 * OS 버전에 따라 요청된 권한이 요청에서 제외될 수 있습니다.
 * 이 경우에는 허용 또는 거부 결과에 포함하지 않도록 처리됩니다.
 *
 * 예시:
 * ```
 * private val launcher = RequestPermissionLauncher(
 *   Manifest.permission.CAMERA,
 *   Manifest.permission.ACCESS_FINE_LOCATION,
 * ) { result ->
 *   result.grantedPermissions // <- 허용된 권한 목록
 *   result.deniedPermissions // <- 거부된 권한 목록
 * }
 *
 * // ...
 *
 * launcher.launch()
 * ```
 */
@Suppress("FunctionName")
fun LifecycleOwner.RequestPermissionLauncher(
  vararg permissions: String,
  block: (RequestPermissionResult) -> Unit,
): RequestPermissionActivityResultLauncher {
  val newPermissions = mutableListOf<String>()
  for (e in permissions) {
    if (Build.VERSION.SDK_INT >= 30 && e == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
      continue
    }

    if (Build.VERSION.SDK_INT >= 33 && e == Manifest.permission.READ_EXTERNAL_STORAGE) {
      continue
    }

    if (Build.VERSION.SDK_INT < 33) {
      if (e == Manifest.permission.READ_MEDIA_VIDEO) {
        continue
      }

      if (e == Manifest.permission.READ_MEDIA_IMAGES) {
        continue
      }

      if (e == Manifest.permission.READ_MEDIA_AUDIO) {
        continue
      }
    }

    newPermissions.add(e)
  }

  return RequestPermissionActivityResultLauncher(newPermissions.toTypedArray()).also {
    ActivityResultImpl(it, ActivityResultContracts.StartActivityForResult()) { resultIntent ->
      if (resultIntent == null) {
        block(
          RequestPermissionResult(
            emptyList(),
            newPermissions.toList(),
          )
        )
        return@ActivityResultImpl
      }

      val grantResults = resultIntent.getIntArrayExtra(
        ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS
      )
      if (grantResults == null) {
        block(
          RequestPermissionResult(
            emptyList(),
            newPermissions.toList(),
          )
        )
        return@ActivityResultImpl
      }

      val grantedPermissions = mutableListOf<String>()
      val deniedPermissions = mutableListOf<String>()
      for (i in newPermissions.indices) {
        val isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
          grantedPermissions.add(newPermissions[i])
        } else {
          deniedPermissions.add(newPermissions[i])
        }
      }

      block(
        RequestPermissionResult(
          grantedPermissions.toList(),
          deniedPermissions.toList(),
        )
      )
    }
  }
}

@Suppress("MemberVisibilityCanBePrivate")
class RequestPermissionActivityResultLauncher(
  private val permissions: Array<out String>,
) : ActivityResultLauncherWrapper() {
  fun launch() {
    launch(null)
  }

  fun launch(options: ActivityOptionsCompat?) {
    mainThreadHandler.post {
      val i = Intent(ActivityResultContracts.RequestMultiplePermissions.ACTION_REQUEST_PERMISSIONS)
      i.putExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS, permissions)
      baseLauncher!!.launch(i, options)
    }
  }
}

data class RequestPermissionResult(
  /**
   * 허용된 권한 목록
   */
  val grantedPermissions: List<String>,

  /**
   * 거부된 권한 목록
   */
  val deniedPermissions: List<String>,
)

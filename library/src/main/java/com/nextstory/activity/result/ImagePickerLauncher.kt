@file:Suppress("unused", "FunctionName", "DEPRECATION")

package com.nextstory.activity.result

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LifecycleOwner
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.features.registerImagePicker
import com.nextstory.R
import com.nextstory.app.AbstractActivity
import com.nextstory.util.finishWithResult
import com.nextstory.util.takeIfOrElse

const val EXTRA_COUNT = "com.nextstory.activity.extra.result.COUNT"
const val EXTRA_RESULT = "com.nextstory.activity.extra.result.RESULT"

/**
 * 이미지 선택
 *
 * 3개 선택 예시:
 * ```
 * private val launcher = ImagePickerLauncher(3) { result ->
 *   if (result.isSuccessful) {
 *     result.uris // 고른 사진의 URI를 목록 형태로 반환됩니다.
 *   } else {
 *     result.failureReason // 해당 변수로 실패 사유에 따라 처리합니다.
 *   }
 * }
 *
 * // ...
 *
 * launcher.launch()
 * ```
 *
 * 단일 선택의 경우에는 한 장의 사진을 고른뒤에 사진 자르기가 진행됩니다.
 *
 * 단일 선택 예시:
 * ```
 * private val launcher = ImagePickerLauncher() { result ->
 *   if (result.isSuccessful) {
 *     result.uris // 고른 사진의 URI를 목록 형태로 반환됩니다.
 *   } else {
 *     result.failureReason // 해당 변수로 실패 사유에 따라 처리합니다.
 *   }
 * }
 *
 * // ...
 *
 * launcher.launch()
 * ```
 *
 * @see ImagePickerResult
 * @see ImagePickerFailureReason
 */
fun LifecycleOwner.ImagePickerLauncher(
  @IntRange(from = 1, to = 30) count: Int = 1,
  block: (ImagePickerResult) -> Unit,
): ImagePickerActivityResultLauncher {
  return ImagePickerActivityResultLauncher(count).also {
    ActivityResultImpl(it, ActivityResultContracts.StartActivityForResult()) { resultIntent ->
      if (resultIntent == null) {
        block(ImagePickerResult(false, ImagePickerFailureReason.Canceled, emptyList()))
        return@ActivityResultImpl
      }

      val result = resultIntent.getParcelableExtra<ImagePickerResult>(EXTRA_RESULT)
      if (result == null) {
        block(ImagePickerResult(false, ImagePickerFailureReason.Unknown, emptyList()))
        return@ActivityResultImpl
      }

      block(result)
    }
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ImagePickerActivity : AbstractActivity() {
  @SuppressLint("InlinedApi")
  private val requestPermissionLauncher = RequestPermissionLauncher(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_VIDEO,
  ) {
    if (it.deniedPermissions.isNotEmpty()) {
      finishWithResult { intent ->
        intent.putExtra(
          EXTRA_RESULT,
          ImagePickerResult(false, ImagePickerFailureReason.PermissionDenied, emptyList())
        )
      }
      return@RequestPermissionLauncher
    }

    val config = ImagePickerConfig().apply {
      theme = R.style.Theme_Nextstory_ImagePicker
      isFolderMode = true
      returnMode = ReturnMode.ALL
      limit = intent.getIntExtra(EXTRA_COUNT, 1)
      mode = (limit == 1).takeIfOrElse(ImagePickerMode.SINGLE, ImagePickerMode.MULTIPLE)
    }
    imagePickerLauncher.launch(config)
  }

  private val imagePickerLauncher = registerImagePicker {
    if (it.isEmpty()) {
      finishWithResult { intent ->
        intent.putExtra(
          EXTRA_RESULT,
          ImagePickerResult(false, ImagePickerFailureReason.Canceled, emptyList())
        )
      }
      return@registerImagePicker
    }

    if (it.size == 1) {
      val config = CropImageContractOptions(
        it[0].uri,
        CropImageOptions().apply {
          // backgroundColor = 0xff3f3f3f.toInt()
          activityBackgroundColor = 0xff3f3f3f.toInt()
        }
      )
      cropImageLauncher.launch(config)
      return@registerImagePicker
    }

    finishWithResult { intent ->
      intent.putExtra(
        EXTRA_RESULT,
        ImagePickerResult(true, ImagePickerFailureReason.None, it.map { image -> image.uri })
      )
    }
  }

  private val cropImageLauncher = registerForActivityResult(CropImageContract()) {
    if (!it.isSuccessful) {
      finishWithResult { intent ->
        intent.putExtra(
          EXTRA_RESULT,
          ImagePickerResult(false, ImagePickerFailureReason.Canceled, emptyList())
        )
      }
      return@registerForActivityResult
    }

    finishWithResult { intent ->
      intent.putExtra(
        EXTRA_RESULT,
        ImagePickerResult(true, ImagePickerFailureReason.None, listOf(it.uriContent!!))
      )
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestPermissionLauncher.launch()
  }

  @Suppress("OVERRIDE_DEPRECATION")
  @SuppressLint("MissingSuperCall")
  override fun onBackPressed() {
    // NOOP: ignored
  }

  fun nothing() {
    // NOOP: ignored
  }
}

@Suppress("MemberVisibilityCanBePrivate")
class ImagePickerActivityResultLauncher(private val count: Int) : ActivityResultLauncherWrapper() {
  fun launch() {
    launch(null)
  }

  fun launch(options: ActivityOptionsCompat?) {
    val i = Intent(context, ImagePickerActivity::class.java)
    i.putExtra(EXTRA_COUNT, count)
    baseLauncher!!.launch(i, options)
  }
}

data class ImagePickerResult(
  /**
   * 성공 유무
   */
  val isSuccessful: Boolean,

  /**
   * 실패 사유
   */
  val failureReason: ImagePickerFailureReason,

  /**
   * 이미지 파일 URI 목록
   *
   * 주의: URI 내용을 직접적으로 편집할 경우 권한 문제가 생길 수 있음!
   *       내부 저장소로 복사하여 처리할 것
   */
  val uris: List<Uri>,
) : Parcelable {
  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeByte((isSuccessful).takeIfOrElse(1, 0))
    parcel.writeString(failureReason.name)
    parcel.writeTypedList(uris)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ImagePickerResult> {
    override fun createFromParcel(parcel: Parcel): ImagePickerResult {
      return ImagePickerResult(
        parcel.readByte() != 0.toByte(),
        ImagePickerFailureReason.valueOf(parcel.readString() ?: ImagePickerFailureReason.None.name),
        parcel.createTypedArrayList(Uri.CREATOR)!!,
      )
    }

    override fun newArray(size: Int): Array<ImagePickerResult?> {
      return arrayOfNulls(size)
    }
  }
}

enum class ImagePickerFailureReason {
  /**
   * 없음
   */
  None,

  /**
   * 사용자가 취소했을때
   */
  Canceled,

  /**
   * 필요 권한 거부되었을때
   */
  PermissionDenied,

  /**
   * OS 또는 기타 오류
   *
   * NOTE: 반드시 필요할 경우만 처리할 것
   */
  Unknown,
}

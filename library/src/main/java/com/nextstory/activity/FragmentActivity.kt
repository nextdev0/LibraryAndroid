@file:Suppress(
  "unused",
  "FunctionName",
  "DEPRECATION",
  "SpellCheckingInspection",
  "RemoveRedundantQualifierName",
)

package com.nextstory.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.nextstory.R
import com.nextstory.activity.result.ActivityResultImpl
import com.nextstory.activity.result.ActivityResultLauncherWrapper
import com.nextstory.app.AbstractActivity
import com.nextstory.app.mainThreadHandler
import com.nextstory.util.Json
import com.nextstory.util.Json.Companion.jsonDecode
import com.nextstory.util.Json.Companion.jsonEncode
import kotlin.properties.ReadOnlyProperty

const val EXTRA_FRAGMENT = "com.nextstory.activity.extra.FRAGMENT"
const val EXTRA_INPUT = "com.nextstory.activity.extra.INPUT"
const val EXTRA_RESULT = "com.nextstory.activity.extra.RESULT"

/**
 * [Fragment] 기반의 액티비티를 시작하는 [Intent] 생성
 *
 * `AndroidManifest.xml`에 액티비티 선언을 하지 않고도 실행할 수 있도록 합니다.
 *
 * 입력값을 전달할 경우 [input]에 값을 입력합니다. [Json] 타입만 지원합니다.
 *
 * 예시:
 * ```
 * val intent = FragmentActivityIntent(this, TestFragment::class.java)
 * startActivity(intent)
 *
 * // ... 입력값이 있을 경우
 *
 * val input = jsonOf(
 *  "bool" to true,
 *  "int" to 123,
 * )
 * val intent = FragmentActivityIntent(this, TestFragment::class.java, input)
 * startActivity(intent)
 * ```
 *
 * @see Json
 */
fun <T : Fragment> FragmentActivityIntent(
  context: Context,
  fragmentClass: Class<T>,
  input: Json = Json.NULL,
) = Intent(context, FragmentLauncherActivity::class.java).also {
  it.putExtra(EXTRA_FRAGMENT, fragmentClass)
  it.putExtra(EXTRA_INPUT, input)
}

/**
 * [Fragment] 기반의 액티비티를 시작하여 결과를 처리할 수 있도록 합니다.
 *
 * 액티비티 열기 예시:
 * ```
 * private val activityResultLauncher = FragmentActivityLauncher<TestFragment> {
 *   if (it == Json.NULL) {
 *     // RESULT_CANCEL
 *     return@fragmentActivityLauncher
 *   }
 *
 *   // RESULT_OK
 *   println(it.strValue)
 * }
 *
 * // ...
 *
 * activityResultLauncher.launch()
 *
 * // 또는 입력값을 포함하여 열기
 *
 * val input = jsonOf("input_value")
 * activityResultLauncher.launch(input)
 * ```
 *
 * 입력값 가져오기 예시:
 * ```
 * class TestFragment : Fragment {
 *   // 입력값이 없을 경우: Json.NULL
 *   private input by fragmentActivityInput()
 * }
 * ```
 *
 * @see FragmentActivityIntent
 * @see androidx.activity.result.contract.ActivityResultContract
 * @see androidx.activity.result.ActivityResultLauncher
 * @see androidx.activity.result.ActivityResultCallback
 */
inline fun <reified T : Fragment> LifecycleOwner.FragmentActivityLauncher(
  crossinline block: (Json) -> Unit,
) = FragmentLauncherActivityLauncher { context, json ->
  FragmentActivityIntent(context, T::class.java, json)
}.also { launcher ->
  val contract = ActivityResultContracts.StartActivityForResult()
  ActivityResultImpl(launcher, contract) { result ->
    if (result == null) {
      block(Json.NULL)
      return@ActivityResultImpl
    }

    val resultJson: Json? = result.getParcelableExtra(EXTRA_RESULT)
    block.invoke(resultJson ?: Json.NULL)
  }
}

/**
 * [FragmentActivityIntent] 입력값을 가지도록 함.
 *
 * SavedState를 지원하여 프로세스가 종료되어도 상태가 유지됩니다.
 *
 * 예시:
 * ```
 * class TestFragment : Fragment {
 *   // 입력값이 없을 경우: Json.NULL
 *   private input by fragmentActivityInput()
 * }
 * ```
 */
fun fragmentActivityInput(): ReadOnlyProperty<Fragment, Json> {
  var json: Json? = null
  return ReadOnlyProperty { fragment, property ->
    if (json == null) {
      val key = "saved_state:fragment_launcher:input:${property.name}"

      val bundle = fragment.savedStateRegistry.consumeRestoredStateForKey(key)
      json = if (bundle != null) {
        val savedState = bundle.getString(key)!!
        savedState.jsonDecode()
      } else {
        val activity = fragment.activity
        if (activity == null) {
          Json.NULL
        } else {
          activity.intent.getParcelableExtra(EXTRA_INPUT) ?: Json.NULL
        }
      }

      fragment.savedStateRegistry.registerSavedStateProvider(key) {
        val outState = Bundle()
        outState.putString(key, json!!.jsonEncode())
        outState
      }
    }

    json!!
  }
}

/**
 * [FragmentActivityIntent] 결과 설정
 */
fun Fragment.setFragmentActivityResult(result: Json) {
  if (activity == null) {
    return
  }

  val intent = Intent()
  intent.putExtra(EXTRA_RESULT, result)
  activity!!.setResult(Activity.RESULT_OK, intent)
}

/**
 * `AndroidManifest.xml`를 대체할 속성
 *
 * 예시:
 * ```
 * @FragmentActivityConfiguration(
 *   orientation = FragmentActivityOrientation.LANDSCAPE,
 * )
 * class TestFragment : Fragment() {
 *   // ...
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentActivityConfiguration(
  val orientation: FragmentActivityOrientation = FragmentActivityOrientation.UNSPECIFIED,
  vararg val windowInputMode: FragmentActivityWindowSoftInputMode = [
    FragmentActivityWindowSoftInputMode.ADJUST_PAN,
    FragmentActivityWindowSoftInputMode.STATE_ALWAYS_HIDDEN,
  ],
)

/**
 * 화면 방향 속성 enum
 */
enum class FragmentActivityOrientation(internal val activityInfo: Int) {
  UNSPECIFIED(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
  LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
  PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
  USER(ActivityInfo.SCREEN_ORIENTATION_USER),
  BEHIND(ActivityInfo.SCREEN_ORIENTATION_BEHIND),
  SENSOR(ActivityInfo.SCREEN_ORIENTATION_SENSOR),
  NOSENSOR(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR),
  SENSOR_LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE),
  SENSOR_PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT),
  REVERSE_LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE),
  REVERSE_PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT),
  FULL_SENSOR(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR),
  USER_LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE),
  USER_PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT),
  FULL_USER(ActivityInfo.SCREEN_ORIENTATION_FULL_USER),
  LOCKED(ActivityInfo.SCREEN_ORIENTATION_LOCKED),
}

/**
 * 키보드 동작 속성 enum
 */
enum class FragmentActivityWindowSoftInputMode(internal val value: Int) {
  STATE_UNSPECIFIED(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED),
  STATE_UNCHANGED(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED),
  STATE_HIDDEN(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN),
  STATE_ALWAYS_HIDDEN(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN),
  STATE_VISIBLE(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE),
  STATE_ALWAYS_VISIBLE(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE),
  ADJUST_UNSPECIFIED(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED),
  ADJUST_RESIZE(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE),
  ADJUST_PAN(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN),
  ADJUST_NOTHING(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING),
  IS_FORWARD_NAVIGATION(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION),
}

class FragmentLauncherActivityLauncher(
  private val intentFactory: (Context, Json) -> Intent,
) : ActivityResultLauncherWrapper() {
  fun launch(input: Json = Json.NULL) {
    mainThreadHandler.post {
      val intent = intentFactory(context!!, input)
      baseLauncher!!.launch(intent)
    }
  }

  fun launch(options: ActivityOptionsCompat?) {
    mainThreadHandler.post {
      val intent = intentFactory(context!!, Json.NULL)
      baseLauncher!!.launch(intent, options)
    }
  }

  fun launch(input: Json, options: ActivityOptionsCompat?) {
    mainThreadHandler.post {
      val intent = intentFactory(context!!, input)
      baseLauncher!!.launch(intent, options)
    }
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FragmentLauncherActivity : AbstractActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_fragment_launcher)

    if (savedInstanceState == null) {
      val fragmentClass = intent.getSerializableExtra(EXTRA_FRAGMENT) as Class<*>
      val fragment = fragmentClass.newInstance() as Fragment
      val config = fragment.javaClass.getAnnotation(FragmentActivityConfiguration::class.java)
      if (config != null) {
        requestedOrientation = config.orientation.activityInfo
        window.setSoftInputMode(config.windowInputMode.fold(0) { result, e -> result or e.value })
      }

      supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_launcher_container, fragment)
        .commit()
    }
  }
}

@file:Suppress("unused", "FunctionName", "SpellCheckingInspection")

package com.nextstory.util

import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.lang.System.nanoTime

/**
 * UI 쓰레드용 [CoroutineScope]
 *
 * 예시:
 * ```
 * class TestActivity : AppCompatActivity {
 *   private val scope = UiScope("test_scope")
 *
 *   override fun onDestroy() {
 *     super.onDestroy()
 *
 *     // 취소
 *     scope.cancel()
 *   }
 * }
 * ```
 *
 * @see [MainScope], 동일한 기능에 이름만 추가됨
 */
fun UiScope(name: String = "unnamed"): CoroutineScope {
  return MainScope() + CoroutineName("ui_scope:$name@${nanoTime()}")
}

/**
 * 특정 수명주기에 취소되는 코루틴 작업 실행
 *
 * 예시:
 * ```
 * val job = launchLifecycleCoroutin(Lifecycle.Event.ON_DESTROY) {
 *   // 작업 추가 ...
 *   // 지정한 수명주기가 발생하면 취소됩니다.
 * }
 *
 * // ...
 *
 * // 직접 취소도 가능합니다.
 * job.cancel()
 * ```
 */
fun LifecycleOwner.launchLifecycleCoroutine(
  cancelOnEvent: Lifecycle.Event,
  block: suspend CoroutineScope.() -> Unit
) = UiScope(cancelOnEvent).launch(block = block)

private fun LifecycleOwner.UiScope(cancelOnEvent: Lifecycle.Event): CoroutineScope {
  val name = CoroutineName("ui_scope:lifecycle:${cancelOnEvent.name.lowercase()}@${nanoTime()}")
  return (MainScope() + name).also {
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
      it.cancel()
      return@also
    }

    lifecycle.addObserver(object : LifecycleEventObserver {
      override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (this@UiScope != source) {
          return
        }

        if (event == cancelOnEvent && it.isActive) {
          it.cancel()
          return
        }

        if (event == Lifecycle.Event.ON_DESTROY) {
          if (it.isActive) {
            it.cancel()
          }
          lifecycle.removeObserver(this)
        }
      }
    })
  }
}

/**
 * 뷰가 해제될때 취소되는 코루틴 작업 실행
 *
 * 예시:
 * ```
 * val job = launchViewCoroutine() {
 *   // 작업 추가 ...
 *   // 뷰가 onDetachedFromWindow가 발생할때 작업이 해제됩니다.
 * }
 *
 * // ...
 *
 * // 직접 취소도 가능합니다.
 * job.cancel()
 * ```
 */
fun View.launchViewCoroutine(block: suspend CoroutineScope.() -> Unit) =
  UiScope().launch(block = block)

private fun View.UiScope(): CoroutineScope {
  val name = CoroutineName("ui_scope:view:on_detached@${nanoTime()}")
  return (MainScope() + name).also {
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        // NOOP: ignored
      }

      override fun onViewDetachedFromWindow(v: View) {
        if (it.isActive) {
          it.cancel()
        }
        removeOnAttachStateChangeListener(this)
      }
    })
  }
}



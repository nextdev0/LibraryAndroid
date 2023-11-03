@file:Suppress("unused", "SpellCheckingInspection")

package com.nextstory.databinding

import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty

/**
 * [ViewDataBinding] 생성 & 해제에 필요한 보일러플레이트 구현
 *
 * 예시:
 * ```
 * private val binding by viewDataBinding<TestBinding>(R.layout.test);
 *
 * // ...
 *
 * override fun onCreate(savedInstanceState: Bundle?) {
 *   super.onCreate(savedInstanceState)
 *   setContentView(binding.root);
 * }
 * ```
 *
 * @param T   [ViewDataBinding] 기반 타입
 * @param res 레이아웃 리소스 ID
 * @return 생성된 바인딩 객체를 반환합니다.
 */
fun <T : ViewDataBinding> viewDataBinding(@LayoutRes res: Int): ReadOnlyProperty<Any?, T> {
  var binding: T? = null
  return ReadOnlyProperty { thiz, _ ->
    if (binding == null) {
      when (thiz) {
        is ComponentActivity -> {
          thiz.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              binding = null
              thiz.lifecycle.removeObserver(this)
            }
          })
          binding = DataBindingUtil.inflate(thiz.layoutInflater, res, null, false)
        }

        is Fragment -> {
          thiz.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              binding = null
              thiz.lifecycle.removeObserver(this)
            }
          })
          binding = DataBindingUtil.inflate(thiz.layoutInflater, res, null, false)
        }

        is View -> {
          val layoutInflater = LayoutInflater.from(thiz.context)
          binding = DataBindingUtil.inflate(layoutInflater, res, null, false)
        }

        else -> {
          throw UnsupportedOperationException()
        }
      }
    }

    binding!!
  }
}

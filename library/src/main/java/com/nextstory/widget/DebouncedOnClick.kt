@file:Suppress("unused")

package com.nextstory.widget

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * 연속 동작 방지 클릭 리스너
 *
 * @see android.view.View.OnClickListener
 */
abstract class DebouncedOnClickListener : View.OnClickListener {
  private var clickedInterval: Long = -1L

  override fun onClick(v: View?) {
    val currentTimes = System.currentTimeMillis()
    val interval = currentTimes - clickedInterval
    if (clickedInterval == -1L || interval >= DebouncedOnClickUtils.CLICK_INTERVAL) {
      clickedInterval = currentTimes
      onDebouncedClick(v)
    }
  }

  abstract fun onDebouncedClick(v: View?)
}

object DebouncedOnClickUtils {
  internal const val CLICK_INTERVAL = 600L

  fun View?.setDebouncedOnClickListener(l: DebouncedOnClickListener?) {
    this?.setOnClickListener(l)
  }

  @JvmStatic
  @BindingAdapter("debounceOnClick")
  fun View?.setDebouncedOnClickListener(l: View.OnClickListener?) {
    this?.setOnClickListener(
      if (l == null) {
        null
      } else {
        object : DebouncedOnClickListener() {
          override fun onDebouncedClick(v: View?) {
            l.onClick(v)
          }
        }
      }
    )
  }
}

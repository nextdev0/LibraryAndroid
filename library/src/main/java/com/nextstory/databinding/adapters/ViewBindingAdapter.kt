@file:Suppress("unused")

package com.nextstory.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {
  @JvmStatic
  @BindingAdapter("isGone")
  fun setVisibilityGone(view: View, enabled: Boolean) {
    view.visibility = if (enabled) View.GONE else View.VISIBLE
  }

  @JvmStatic
  @BindingAdapter("isNotGone")
  fun setVisibilityGoneInverse(view: View, enabled: Boolean) {
    view.visibility = if (enabled) View.VISIBLE else View.GONE
  }

  @JvmStatic
  @BindingAdapter("isInvisible")
  fun setVisibilityInvisible(view: View, enabled: Boolean) {
    view.visibility = if (enabled) View.INVISIBLE else View.VISIBLE
  }

  @JvmStatic
  @BindingAdapter("isNotInvisible")
  fun setVisibilityInvisibleInverse(view: View, enabled: Boolean) {
    view.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
  }
}

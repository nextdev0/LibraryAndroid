@file:Suppress("unused")

package com.nextstory.databinding.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter

object ConstraintLayoutBindingAdapter {
  @JvmStatic
  @BindingAdapter("layout_constraintWidth_max_percentage")
  fun setConstraintWidthMaxPercentage(view: View, percentage: Float) {
    if (view.layoutParams != null && view.layoutParams is ConstraintLayout.LayoutParams) {
      val params = view.layoutParams as ConstraintLayout.LayoutParams
      val screenWidth = view.resources.displayMetrics.widthPixels
      params.matchConstraintMaxWidth = (screenWidth * percentage).toInt()
      view.requestLayout()
    }
  }

  @JvmStatic
  @BindingAdapter("layout_constraintHeight_max_percentage")
  fun setConstraintHeightMaxPercentage(view: View, percentage: Float) {
    if (view.layoutParams != null && view.layoutParams is ConstraintLayout.LayoutParams) {
      val params = view.layoutParams as ConstraintLayout.LayoutParams
      val screenHeight = view.resources.displayMetrics.heightPixels
      params.matchConstraintMaxHeight = (screenHeight * percentage).toInt()
      view.requestLayout()
    }
  }
}

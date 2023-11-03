@file:Suppress("unused")

package com.nextstory.databinding.adapters

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.graphics.TypefaceCompat
import androidx.databinding.BindingAdapter
import com.nextstory.util.Action1
import com.nextstory.util.Action2

object TextViewBindingAdapter {
  private val textFontWeightTypefaceCache = mutableMapOf<Int, Typeface>()

  @JvmStatic
  @BindingAdapter("textFontWeightCompat")
  fun setFontWeight(view: TextView, weight: Int) {
    if (!textFontWeightTypefaceCache.containsKey(weight)) {
      val typeface = TypefaceCompat.create(view.context, null, weight, false)
      textFontWeightTypefaceCache[weight] = typeface
    }
    view.typeface = textFontWeightTypefaceCache[weight]
  }

  @JvmStatic
  @BindingAdapter("onEditorAction")
  fun setOnEditorActionListener(view: TextView, l: Action1<View>) {
    view.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (v != null && actionId == view.imeOptions) {
          l(v)
          return true
        }
        return false
      }
    })
  }

  @JvmStatic
  @BindingAdapter("onTextChanged")
  fun setOnTextChanged(view: TextView, l: Action2<View, String>) {
    view.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // NOOP: ignored
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // NOOP: ignored
      }

      override fun afterTextChanged(s: Editable?) {
        l(view, s.toString())
      }
    })
  }
}

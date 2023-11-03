@file:Suppress("unused")

package com.nextstory.app

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

/**
 * 기본 기능이 포함된 [Fragment]
 *
 * 포함된 항목:
 * * 뒤로가기 콜백
 */
abstract class AbstractFragment : Fragment() {
  private val backPressedCallback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      onBackPressed()
      isEnabled = true
    }
  }

  open fun onBackPressed() {
    try {
      if (isVisible) {
        backPressedCallback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
      }
    } catch (ignored: Exception) {
      backPressedCallback.isEnabled = false
      requireActivity().onBackPressedDispatcher.onBackPressed()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
  }
}

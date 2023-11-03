package com.nextstory.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nextstory.activity.FragmentActivityConfiguration
import com.nextstory.activity.FragmentActivityOrientation
import com.nextstory.activity.FragmentActivityWindowSoftInputMode
import com.nextstory.activity.fragmentActivityInput
import com.nextstory.activity.setFragmentActivityResult
import com.nextstory.app.AbstractFragment
import com.nextstory.sample.databinding.FragmentTestBinding
import com.nextstory.util.savedStateJsonOf
import com.nextstory.databinding.viewDataBinding

@FragmentActivityConfiguration(
  orientation = FragmentActivityOrientation.LANDSCAPE,
  windowInputMode = [
    FragmentActivityWindowSoftInputMode.ADJUST_RESIZE,
    FragmentActivityWindowSoftInputMode.STATE_ALWAYS_HIDDEN,
  ],
)
class TestFragment : AbstractFragment() {
  private val binding by viewDataBinding<FragmentTestBinding>(R.layout.fragment_test)

  val data1 by fragmentActivityInput()
  val data2 by savedStateJsonOf(0)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding.lifecycleOwner = this
    binding.fragment = this

    binding.btn1.setOnClickListener {
      data1.intValue++
    }

    binding.btn2.setOnClickListener {
      data2.intValue++
    }

    setFragmentActivityResult(data2)

    return binding.root
  }

  override fun onBackPressed() {
    super.onBackPressed()
  }
}

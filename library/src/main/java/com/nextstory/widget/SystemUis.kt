@file:Suppress("unused")

package com.nextstory.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.nextstory.util.dpi
import com.nextstory.util.getNavigationBarHeight
import com.nextstory.util.getStatusBarHeight
import com.nextstory.util.takeIfOrElse

/**
 * 상태바 높이를 갖는 뷰
 */
class StatusSpace : View {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val size = (isInEditMode).takeIfOrElse((24 * dpi).toInt(), getStatusBarHeight())
    setMeasuredDimension(
      (layoutParams.width == -1).takeIfOrElse(size, 0),
      (layoutParams.height == -1).takeIfOrElse(size, 0),
    )
  }

  @SuppressLint("MissingSuperCall")
  override fun draw(canvas: Canvas) {
    // NOOP: 그리기 없음
  }

  override fun onDrawForeground(canvas: Canvas) {
    // NOOP: 그리기 없음
  }
}

/**
 * 내비게이션 바 높이를 갖는 뷰
 */
class NavigationSpace : View {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val size = (isInEditMode).takeIfOrElse((48 * dpi).toInt(), getNavigationBarHeight())
    setMeasuredDimension(
      (layoutParams.width == -1).takeIfOrElse(size, 0),
      (layoutParams.height == -1).takeIfOrElse(size, 0),
    )
  }

  @SuppressLint("MissingSuperCall")
  override fun draw(canvas: Canvas) {
    // NOOP: 그리기 없음
  }

  override fun onDrawForeground(canvas: Canvas) {
    // NOOP: 그리기 없음
  }
}

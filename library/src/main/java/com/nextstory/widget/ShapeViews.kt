@file:Suppress("unused", "DEPRECATION", "OVERRIDE_DEPRECATION", "SENSELESS_COMPARISON")

package com.nextstory.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.nextstory.R
import com.nextstory.util.takeIfOrElse

class ShapeFrameLayout : FrameLayout, ShapeView {
  private val helper = ShapeViewHelper(this)

  override var cornerRadius: Int
    get() = helper.cornerRadius
    set(value) {
      helper.cornerRadius = value
      invalidate()
    }

  override var strokeWidth: Int
    get() = helper.strokeWidth
    set(value) {
      helper.strokeWidth = value
      invalidate()
    }

  override var strokeColor: Int
    get() = helper.strokeColor
    set(value) {
      helper.strokeColor = value
      invalidate()
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    helper.resolveAttributeSets(context, attrs, super.getBackground())

    super.setBackgroundDrawable(helper.drawable)
    super.setClipToOutline(true)
    super.setOutlineProvider(object : ViewOutlineProvider() {
      override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(helper.drawable.bounds, helper.cornerRadius.toFloat())
      }
    })
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    helper.shapeDrawable.draw(canvas)
  }

  override fun setBackgroundResource(resId: Int) {
    setBackgroundDrawable(ContextCompat.getDrawable(context, resId))
  }

  override fun setBackgroundColor(color: Int) {
    setBackgroundDrawable(ColorDrawable(color))
  }

  override fun setBackground(background: Drawable?) {
    setBackgroundDrawable(background)
  }

  override fun setBackgroundDrawable(background: Drawable?) {
    if (helper != null && helper.isResolvedAttributeSets) {
      helper.drawable.setDrawable(0, background)
      invalidate()
      return
    }
    super.setBackgroundDrawable(background)
  }

  override fun getBackground(): Drawable {
    if (helper != null && helper.isResolvedAttributeSets) {
      return helper.drawable.getDrawable(0)
    }
    return super.getBackground()
  }
}

class ShapeTextView : AppCompatTextView, ShapeView {
  private val helper = ShapeViewHelper(this)

  override var cornerRadius: Int
    get() = helper.cornerRadius
    set(value) {
      helper.cornerRadius = value
      invalidate()
    }

  override var strokeWidth: Int
    get() = helper.strokeWidth
    set(value) {
      helper.strokeWidth = value
      invalidate()
    }

  override var strokeColor: Int
    get() = helper.strokeColor
    set(value) {
      helper.strokeColor = value
      invalidate()
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?)
    : this(context, attrs, android.R.attr.textViewStyle)

  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    helper.resolveAttributeSets(context, attrs, super.getBackground())

    super.setBackgroundDrawable(helper.drawable)
    super.setClipToOutline(true)
    super.setOutlineProvider(object : ViewOutlineProvider() {
      override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(helper.drawable.bounds, helper.cornerRadius.toFloat())
      }
    })
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    helper.shapeDrawable.draw(canvas)
  }

  override fun setBackgroundResource(resId: Int) {
    setBackgroundDrawable(ContextCompat.getDrawable(context, resId))
  }

  override fun setBackgroundColor(color: Int) {
    setBackgroundDrawable(ColorDrawable(color))
  }

  override fun setBackground(background: Drawable?) {
    setBackgroundDrawable(background)
  }

  override fun setBackgroundDrawable(background: Drawable?) {
    if (helper != null && helper.isResolvedAttributeSets) {
      helper.drawable.setDrawable(0, background)
      invalidate()
      return
    }
    super.setBackgroundDrawable(background)
  }

  override fun getBackground(): Drawable {
    if (helper != null && helper.isResolvedAttributeSets) {
      return helper.drawable.getDrawable(0)
    }
    return super.getBackground()
  }
}

class ShapeEditText : AppCompatEditText, ShapeView {
  private val helper = ShapeViewHelper(this)

  override var cornerRadius: Int
    get() = helper.cornerRadius
    set(value) {
      helper.cornerRadius = value
      invalidate()
    }

  override var strokeWidth: Int
    get() = helper.strokeWidth
    set(value) {
      helper.strokeWidth = value
      invalidate()
    }

  override var strokeColor: Int
    get() = helper.strokeColor
    set(value) {
      helper.strokeColor = value
      invalidate()
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?)
    : this(context, attrs, androidx.appcompat.R.attr.editTextStyle)

  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    helper.resolveAttributeSets(context, attrs, super.getBackground())

    super.setBackgroundDrawable(helper.drawable)
    super.setClipToOutline(true)
    super.setOutlineProvider(object : ViewOutlineProvider() {
      override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(helper.drawable.bounds, helper.cornerRadius.toFloat())
      }
    })
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    helper.shapeDrawable.draw(canvas)
  }

  override fun setBackgroundResource(resId: Int) {
    setBackgroundDrawable(ContextCompat.getDrawable(context, resId))
  }

  override fun setBackgroundColor(color: Int) {
    setBackgroundDrawable(ColorDrawable(color))
  }

  override fun setBackground(background: Drawable?) {
    setBackgroundDrawable(background)
  }

  override fun setBackgroundDrawable(background: Drawable?) {
    if (helper != null && helper.isResolvedAttributeSets) {
      helper.drawable.setDrawable(0, background)
      invalidate()
      return
    }
    super.setBackgroundDrawable(background)
  }

  override fun getBackground(): Drawable {
    if (helper != null && helper.isResolvedAttributeSets) {
      return helper.drawable.getDrawable(0)
    }
    return super.getBackground()
  }
}

class ShapeImageView : AppCompatImageView, ShapeView {
  private val helper = ShapeViewHelper(this)

  override var cornerRadius: Int
    get() = helper.cornerRadius
    set(value) {
      helper.cornerRadius = value
      invalidate()
    }

  override var strokeWidth: Int
    get() = helper.strokeWidth
    set(value) {
      helper.strokeWidth = value
      invalidate()
    }

  override var strokeColor: Int
    get() = helper.strokeColor
    set(value) {
      helper.strokeColor = value
      invalidate()
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    helper.resolveAttributeSets(context, attrs, super.getBackground())

    super.setBackgroundDrawable(helper.drawable)
    super.setClipToOutline(true)
    super.setOutlineProvider(object : ViewOutlineProvider() {
      override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(helper.drawable.bounds, helper.cornerRadius.toFloat())
      }
    })
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    helper.shapeDrawable.draw(canvas)
  }

  override fun setBackgroundResource(resId: Int) {
    setBackgroundDrawable(ContextCompat.getDrawable(context, resId))
  }

  override fun setBackgroundColor(color: Int) {
    setBackgroundDrawable(ColorDrawable(color))
  }

  override fun setBackground(background: Drawable?) {
    setBackgroundDrawable(background)
  }

  override fun setBackgroundDrawable(background: Drawable?) {
    if (helper != null && helper.isResolvedAttributeSets) {
      helper.drawable.setDrawable(0, background)
      invalidate()
      return
    }
    super.setBackgroundDrawable(background)
  }

  override fun getBackground(): Drawable {
    if (helper != null && helper.isResolvedAttributeSets) {
      return helper.drawable.getDrawable(0)
    }
    return super.getBackground()
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ShapeViewHelper(private val view: ShapeView) : ShapeView {
  val shapeDrawable = GradientDrawable()
  var isResolvedAttributeSets = false
  var drawable: LayerDrawable = LayerDrawable(arrayOf(ColorDrawable(Color.TRANSPARENT)))

  private var _cornerRadius = 0
  override var cornerRadius: Int
    get() = _cornerRadius
    set(value) {
      _cornerRadius = value
      shapeDrawable.cornerRadius = _cornerRadius.toFloat()
    }

  private var _strokeWidth = 0
  override var strokeWidth: Int
    get() = _strokeWidth
    set(value) {
      _strokeWidth = value
      shapeDrawable.setStroke(_strokeWidth, _strokeColor)
    }

  private var _strokeColor = Color.TRANSPARENT
  override var strokeColor: Int
    get() = _strokeColor
    set(value) {
      _strokeColor = value
      shapeDrawable.setStroke(_strokeWidth, _strokeColor)
    }

  fun resolveAttributeSets(
    context: Context,
    attrs: AttributeSet?,
    backgroundDrawable: Drawable? = null
  ) {
    drawable = LayerDrawable(
      arrayOf(
        (backgroundDrawable == null).takeIfOrElse(
          ColorDrawable(Color.TRANSPARENT),
          backgroundDrawable,
        ),
        shapeDrawable,
      )
    )

    if (attrs == null) {
      isResolvedAttributeSets = true
      return
    }

    val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeView)
    cornerRadius = a.getDimensionPixelSize(R.styleable.ShapeView_shape_cornerRadius, 0)
    strokeWidth = a.getDimensionPixelSize(R.styleable.ShapeView_shape_strokeWidth, 0)
    strokeColor = a.getColor(R.styleable.ShapeView_shape_strokeColor, Color.TRANSPARENT)
    a.recycle()

    isResolvedAttributeSets = true
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
interface ShapeView {
  var cornerRadius: Int
  var strokeWidth: Int
  var strokeColor: Int
}

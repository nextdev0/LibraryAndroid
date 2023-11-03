@file:Suppress(
  "unused",
  "RedundantGetter",
  "MemberVisibilityCanBePrivate",
  "SENSELESS_COMPARISON",
)

package com.nextstory.widget

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewParent
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nextstory.R
import com.nextstory.util.dpi
import com.nextstory.util.getNavigationBarHeight
import com.nextstory.util.getStatusBarHeight
import com.nextstory.util.hasGestureBar
import com.nextstory.util.takeIfOrElse

class WindowConfig : FrameLayout, DefaultLifecycleObserver {
  private val supportNavBarPaint = Paint().apply { color = 0x33000000 }

  private val paddingRect = Rect()
  private val systemInsetsRect = Rect()

  private var paddingSelfChanged = false
  private var initialized = false

  var type: Type = Type.Normal
    get() = field
    set(value) {
      field = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  var useStatusBarDarkIcon: Boolean = false
    get() = field
    set(value) {
      field = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  var useNavigationBarDarkIcon: Boolean = false
    get() = field
    set(value) {
      field = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  val statusPaint = Paint()
  var statusBarColor: Int
    get() = statusPaint.color
    set(value) {
      statusPaint.color = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  val navigationPaint = Paint()
  var navigationBarColor: Int
    get() = navigationPaint.color
    set(value) {
      navigationPaint.color = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  val navigationDividerPaint = Paint()
  var navigationBarDividerColor: Int
    get() = navigationDividerPaint.color
    set(value) {
      navigationDividerPaint.color = value

      if (initialized) {
        updateWindow()
        invalidate()
        requestLayout()
      }
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    setWillNotDraw(false)

    if (attrs == null) {
      paddingRect.set(0, 0, 0, 0)

      type = Type.Normal

      useStatusBarDarkIcon = context.resources.getBoolean(R.bool.is_lightMode)
      useNavigationBarDarkIcon = context.resources.getBoolean(R.bool.is_lightMode)

      statusBarColor = ContextCompat.getColor(context, R.color.light)
      navigationBarColor = ContextCompat.getColor(context, R.color.light)
      navigationBarDividerColor = ContextCompat.getColor(context, R.color.dark_10)
    } else {
      val a = context.obtainStyledAttributes(attrs, R.styleable.WindowConfig)

      type = Type.fromInteger(
        a.getInt(
          R.styleable.WindowConfig_wcfg_type,
          Type.Normal.value
        )
      )

      useStatusBarDarkIcon = a.getBoolean(
        R.styleable.WindowConfig_wcfg_useStatusBarDarkIcon,
        context.resources.getBoolean(R.bool.is_lightMode)
      )

      useNavigationBarDarkIcon = a.getBoolean(
        R.styleable.WindowConfig_wcfg_useNavigationBarDarkIcon,
        context.resources.getBoolean(R.bool.is_lightMode)
      )

      statusBarColor = a.getColor(
        R.styleable.WindowConfig_wcfg_statusBarColor,
        ContextCompat.getColor(context, R.color.light)
      )

      navigationBarColor = a.getColor(
        R.styleable.WindowConfig_wcfg_navigationBarColor,
        ContextCompat.getColor(context, R.color.light)
      )

      navigationBarDividerColor = a.getColor(
        R.styleable.WindowConfig_wcfg_navigationBarDividerColor,
        ContextCompat.getColor(context, R.color.dark_10)
      )

      if (a.hasValue(R.styleable.WindowConfig_android_padding)) {
        val padding = a.getDimensionPixelSize(R.styleable.WindowConfig_android_padding, 0)
        setPadding(padding, padding, padding, padding)
      } else {
        val useRtl = layoutDirection == LAYOUT_DIRECTION_RTL
        setPadding(
          // left
          if (a.hasValue(R.styleable.WindowConfig_android_paddingHorizontal)) {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingHorizontal, 0)
          } else {
            a.getDimensionPixelSize(
              R.styleable.WindowConfig_android_paddingLeft,
              if (useRtl) {
                a.getDimensionPixelOffset(R.styleable.WindowConfig_android_paddingEnd, 0)
              } else {
                a.getDimensionPixelOffset(R.styleable.WindowConfig_android_paddingStart, 0)
              }
            )
          },

          // top
          if (a.hasValue(R.styleable.WindowConfig_android_paddingVertical)) {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingVertical, 0)
          } else {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingTop, 0)
          },

          // right
          if (a.hasValue(R.styleable.WindowConfig_android_paddingHorizontal)) {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingHorizontal, 0)
          } else {
            a.getDimensionPixelSize(
              R.styleable.WindowConfig_android_paddingRight,
              if (useRtl) {
                a.getDimensionPixelOffset(R.styleable.WindowConfig_android_paddingStart, 0)
              } else {
                a.getDimensionPixelOffset(R.styleable.WindowConfig_android_paddingEnd, 0)
              }
            )
          },

          // bottom
          if (a.hasValue(R.styleable.WindowConfig_android_paddingVertical)) {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingVertical, 0)
          } else {
            a.getDimensionPixelSize(R.styleable.WindowConfig_android_paddingBottom, 0)
          }
        )
      }

      a.recycle()

      initialized = true
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    if (!isInEditMode) {
      (context as ComponentActivity).lifecycle.addObserver(this)
    }

    var parentView: ViewParent? = parent
    while (true) {
      if (parentView == null)
        break

      if (parentView is WindowConfig) {
        throw IllegalStateException("WindowConfig has already been defined.")
      }

      parentView = parentView.parent
    }

    updateWindow()
    requestLayout()
  }

  override fun onDetachedFromWindow() {
    if (!isInEditMode) {
      (context as ComponentActivity).lifecycle.removeObserver(this)
    }
    super.onDetachedFromWindow()
  }

  override fun getPaddingLeft(): Int {
    return paddingRect.left
  }

  override fun getPaddingStart(): Int {
    return if (layoutDirection == LAYOUT_DIRECTION_RTL) {
      paddingRect.right
    } else {
      paddingRect.left
    }
  }

  override fun getPaddingTop(): Int {
    return paddingRect.top
  }

  override fun getPaddingRight(): Int {
    return paddingRect.right
  }

  override fun getPaddingEnd(): Int {
    return if (layoutDirection == LAYOUT_DIRECTION_RTL) {
      paddingRect.left
    } else {
      paddingRect.right
    }
  }

  override fun getPaddingBottom(): Int {
    return paddingRect.bottom
  }

  override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
    paddingRect.set(left, top, right, bottom)
    requestLayout()
  }

  override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
    val useRtl = layoutDirection == LAYOUT_DIRECTION_RTL
    paddingRect.set(if (useRtl) end else start, top, if (useRtl) start else end, bottom)
    requestLayout()
  }

  override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
    requestLayout()
    invalidate()
    return super.onApplyWindowInsets(insets)
  }

  override fun requestLayout() {
    if (paddingRect == null || systemInsetsRect == null) {
      super.requestLayout()
      return
    }

    if (!paddingSelfChanged) {
      paddingSelfChanged = true

      if (isInEditMode) {
        val statusSize = (type == Type.Normal).takeIfOrElse(getStatusBarHeight(), 0)
        val navSize = (type == Type.Normal).takeIfOrElse(getNavigationBarHeight(), 0)
        systemInsetsRect.set(0, statusSize, 0, navSize)
      } else {
        val rootWindowInsets = ViewCompat.getRootWindowInsets(rootView)
        if (rootWindowInsets != null) {
          systemInsetsRect.set(0, 0, 0, 0)

          val statusSize = (type == Type.Normal).takeIfOrElse(getStatusBarHeight(), 0)
          val statusBar = rootWindowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
          systemInsetsRect.left += (statusBar.left > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.top += (statusBar.top > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.right += (statusBar.right > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.bottom += (statusBar.bottom > 0).takeIfOrElse(statusSize, 0)

          val ime = rootWindowInsets.getInsets(WindowInsetsCompat.Type.ime())
          if (ime.left > 0 || ime.top > 0 || ime.right > 0 || ime.bottom > 0) {
            systemInsetsRect.left += ime.left
            systemInsetsRect.top += ime.top
            systemInsetsRect.right += ime.right
            systemInsetsRect.bottom += ime.bottom
          } else {
            val navSize = (type != Type.Fullscreen && !hasGestureBar())
              .takeIfOrElse(getNavigationBarHeight(), 0)
            val navBar = rootWindowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            systemInsetsRect.left += (navBar.left > 0).takeIfOrElse(navSize, 0)
            systemInsetsRect.top += (navBar.top > 0).takeIfOrElse(navSize, 0)
            systemInsetsRect.right += (navBar.right > 0).takeIfOrElse(navSize, 0)
            systemInsetsRect.bottom += (navBar.bottom > 0).takeIfOrElse(navSize, 0)
          }
        }
      }

      super.setPadding(
        paddingRect.left + systemInsetsRect.left,
        paddingRect.top + systemInsetsRect.top,
        paddingRect.right + systemInsetsRect.right,
        paddingRect.bottom + systemInsetsRect.bottom
      )
      super.invalidate()
      super.requestLayout()

      paddingSelfChanged = false
    }
  }

  override fun onDraw(canvas: Canvas) {
    if (type != Type.Fullscreen) {
      if (isInEditMode) {
        val statusBar = Insets.of(0, getStatusBarHeight(), 0, 0)
        drawSystemBar(canvas, statusBar, statusPaint)

        val navBar = Insets.of(0, 0, 0, getNavigationBarHeight())
        drawSystemBar(canvas, navBar, navigationPaint)
      } else {
        val insets = ViewCompat.getRootWindowInsets(rootView)
        if (insets != null) {
          val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
          drawSystemBar(canvas, statusBar, statusPaint)

          val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
          drawSystemBar(canvas, navBar, navigationPaint)
        }
      }
    }

    if (type != Type.Fullscreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      if (isInEditMode) {
        val navBar = Insets.of(0, 0, 0, getNavigationBarHeight())
        drawNavigationBarDivider(canvas, navBar)
        return
      }

      val insets = ViewCompat.getRootWindowInsets(rootView)
      if (insets != null) {
        val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        drawNavigationBarDivider(canvas, navBar)
      }
    }
  }

  override fun onDrawForeground(canvas: Canvas) {
    super.onDrawForeground(canvas)

    if (type != Type.Fullscreen) {
      val insets = ViewCompat.getRootWindowInsets(rootView)
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1 && insets != null) {
        val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        drawSystemBar(canvas, navBar, supportNavBarPaint)
      }
    }
  }

  private fun drawSystemBar(canvas: Canvas, insets: Insets, paint: Paint) {
    if (insets.left > 0) {
      canvas.drawRect(0f, 0f, insets.left.toFloat(), height.toFloat(), paint)
    }

    if (insets.top > 0) {
      canvas.drawRect(0f, 0f, width.toFloat(), insets.top.toFloat(), paint)
    }

    if (insets.right > 0) {
      canvas.drawRect(
        (width - insets.right).toFloat(),
        0f,
        width.toFloat(),
        height.toFloat(),
        paint
      )
    }

    if (insets.bottom > 0) {
      canvas.drawRect(
        0f,
        (height - insets.bottom).toFloat(),
        width.toFloat(),
        height.toFloat(),
        paint
      )
    }
  }

  private fun drawNavigationBarDivider(canvas: Canvas, insets: Insets) {
    if (insets.left > 0) {
      canvas.drawRect(
        (insets.left - dpi),
        0f,
        insets.left.toFloat(),
        height.toFloat(),
        navigationDividerPaint
      )
    }

    if (insets.top > 0) {
      canvas.drawRect(
        0f,
        (insets.top - dpi),
        width.toFloat(),
        insets.top.toFloat(),
        navigationDividerPaint
      )
    }

    if (insets.right > 0) {
      canvas.drawRect(
        (width - insets.right).toFloat(),
        0f,
        (width - insets.right + dpi),
        height.toFloat(),
        navigationDividerPaint
      )
    }

    if (insets.bottom > 0) {
      canvas.drawRect(
        0f,
        (height - insets.bottom).toFloat(),
        width.toFloat(),
        (height - insets.bottom + dpi),
        navigationDividerPaint
      )
    }
  }

  private fun updateWindow() {
    if (!isInEditMode) {
      val window = (context as Activity).window
      val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
      windowInsetsController.systemBarsBehavior = (type == Type.Fullscreen).takeIfOrElse(
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE,
        WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
      )

      if (type == Type.Fullscreen) {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
      } else {
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
      }

      windowInsetsController.isAppearanceLightStatusBars = useStatusBarDarkIcon

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        windowInsetsController.isAppearanceLightNavigationBars = useNavigationBarDarkIcon
      }
    }
  }

  override fun onResume(owner: LifecycleOwner) {
    updateWindow()
    invalidate()
    requestLayout()
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    (state as SavedState).also {
      super.onRestoreInstanceState(it.superState)

      type = it.type

      updateWindow()
      invalidate()
      requestLayout()
    }
  }

  override fun onSaveInstanceState(): Parcelable {
    return SavedState(super.onSaveInstanceState()!!).also {
      it.type = type
    }
  }

  enum class Type(val value: Int) {
    Normal(0),
    Extended(1),
    Fullscreen(2);

    companion object {
      fun fromInteger(value: Int): Type {
        return entries.first { it.value == value }
      }
    }
  }

  class SavedState : BaseSavedState {
    var type = Type.Normal

    constructor(superState: Parcelable) : super(superState)
    constructor(parcel: Parcel) : super(parcel) {
      type = Type.fromInteger(parcel.readInt())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
      super.writeToParcel(parcel, flags)
      parcel.writeInt(type.value)
    }

    override fun describeContents(): Int {
      return 0
    }

    companion object CREATOR : Parcelable.Creator<SavedState> {
      override fun createFromParcel(parcel: Parcel): SavedState {
        return SavedState(parcel)
      }

      override fun newArray(size: Int): Array<SavedState?> {
        return arrayOfNulls(size)
      }
    }
  }
}

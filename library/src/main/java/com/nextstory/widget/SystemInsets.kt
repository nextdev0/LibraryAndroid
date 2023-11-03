@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantGetter")

package com.nextstory.widget

import android.content.Context
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nextstory.R
import com.nextstory.util.getNavigationBarHeight
import com.nextstory.util.getStatusBarHeight
import com.nextstory.util.hasGestureBar
import com.nextstory.util.takeIfOrElse

/**
 * 시스템 여백을 갖는 레이아웃
 */
class SystemInsets : FrameLayout {
  private val paddingRect = Rect()
  private val systemInsetsRect = Rect()

  private var paddingSelfChanged = false
  private var initialized = false

  var statusBar: StatusType = StatusType.Disabled
    get() = field
    set(value) {
      field = value

      if (initialized) {
        requestLayout()
      }
    }

  var navigationBar: NavigationType = NavigationType.Disabled
    get() = field
    set(value) {
      field = value

      if (initialized) {
        requestLayout()
      }
    }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    if (attrs == null) {
      statusBar = StatusType.Disabled
      navigationBar = NavigationType.Disabled
      paddingRect.set(0, 0, 0, 0)
    } else {
      val a = context.obtainStyledAttributes(attrs, R.styleable.SystemInsets)

      statusBar = StatusType.fromInteger(
        a.getInt(
          R.styleable.SystemInsets_sis_statusBar,
          StatusType.Disabled.value
        )
      )

      navigationBar = NavigationType.fromInteger(
        a.getInt(
          R.styleable.SystemInsets_sis_navigationBar,
          NavigationType.Disabled.value
        )
      )

      if (a.hasValue(R.styleable.SystemInsets_android_padding)) {
        val padding = a.getDimensionPixelSize(R.styleable.SystemInsets_android_padding, 0)
        setPadding(padding, padding, padding, padding)
      } else {
        val useRtl = layoutDirection == LAYOUT_DIRECTION_RTL
        setPadding(
          // left
          if (a.hasValue(R.styleable.SystemInsets_android_paddingHorizontal)) {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingHorizontal, 0)
          } else {
            a.getDimensionPixelSize(
              R.styleable.SystemInsets_android_paddingLeft,
              if (useRtl) {
                a.getDimensionPixelOffset(R.styleable.SystemInsets_android_paddingEnd, 0)
              } else {
                a.getDimensionPixelOffset(R.styleable.SystemInsets_android_paddingStart, 0)
              }
            )
          },

          // top
          if (a.hasValue(R.styleable.SystemInsets_android_paddingVertical)) {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingVertical, 0)
          } else {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingTop, 0)
          },

          // right
          if (a.hasValue(R.styleable.SystemInsets_android_paddingHorizontal)) {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingHorizontal, 0)
          } else {
            a.getDimensionPixelSize(
              R.styleable.SystemInsets_android_paddingRight,
              if (useRtl) {
                a.getDimensionPixelOffset(R.styleable.SystemInsets_android_paddingStart, 0)
              } else {
                a.getDimensionPixelOffset(R.styleable.SystemInsets_android_paddingEnd, 0)
              }
            )
          },

          // bottom
          if (a.hasValue(R.styleable.SystemInsets_android_paddingVertical)) {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingVertical, 0)
          } else {
            a.getDimensionPixelSize(R.styleable.SystemInsets_android_paddingBottom, 0)
          },
        )
      }

      a.recycle()

      initialized = true
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    requestLayout()
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
    return super.onApplyWindowInsets(insets)
  }

  @Suppress("SENSELESS_COMPARISON")
  override fun requestLayout() {
    if (paddingRect == null || systemInsetsRect == null) {
      super.requestLayout()
      return
    }

    if (!paddingSelfChanged) {
      paddingSelfChanged = true

      if (isInEditMode) {
        val statusSize = (statusBar == StatusType.Enabled).takeIfOrElse(getStatusBarHeight(), 0)
        val navSize = (navigationBar != NavigationType.Disabled &&
          !(navigationBar == NavigationType.NavigationOnly && hasGestureBar()))
          .takeIfOrElse(getNavigationBarHeight(), 0)
        systemInsetsRect.set(0, statusSize, 0, navSize)
      } else {
        systemInsetsRect.set(0, 0, 0, 0)

        val rootWindowInsets = ViewCompat.getRootWindowInsets(rootView)
        if (rootWindowInsets != null) {
          val statusSize = (statusBar == StatusType.Enabled).takeIfOrElse(getStatusBarHeight(), 0)
          val statusBar = rootWindowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
          systemInsetsRect.left += (statusBar.left > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.top += (statusBar.top > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.right += (statusBar.right > 0).takeIfOrElse(statusSize, 0)
          systemInsetsRect.bottom += (statusBar.bottom > 0).takeIfOrElse(statusSize, 0)

          if (!(navigationBar == NavigationType.NavigationOnly && hasGestureBar())) {
            val navSize = (navigationBar != NavigationType.Disabled)
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
        paddingRect.bottom + systemInsetsRect.bottom,
      )
      super.invalidate()
      super.requestLayout()

      paddingSelfChanged = false
    }
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    (state as SavedState).also {
      super.onRestoreInstanceState(it.superState)

      statusBar = it.statusBar
      navigationBar = it.navigationBar

      requestLayout()
    }
  }

  override fun onSaveInstanceState(): Parcelable {
    return SavedState(super.onSaveInstanceState()!!).also {
      it.statusBar = statusBar
      it.navigationBar = navigationBar
    }
  }

  enum class StatusType(val value: Int) {
    Enabled(0),
    Disabled(1);

    companion object {
      fun fromInteger(value: Int): StatusType {
        return StatusType.values().first { it.value == value }
      }
    }
  }

  enum class NavigationType(val value: Int) {
    NavigationOnly(0),
    Enabled(1),
    Disabled(2);

    companion object {
      fun fromInteger(value: Int): NavigationType {
        return NavigationType.values().first { it.value == value }
      }
    }
  }

  class SavedState : BaseSavedState {
    var statusBar = StatusType.Disabled
    var navigationBar = NavigationType.Disabled

    constructor(superState: Parcelable) : super(superState)
    constructor(parcel: Parcel) : super(parcel) {
      statusBar = StatusType.fromInteger(parcel.readInt())
      navigationBar = NavigationType.fromInteger(parcel.readInt())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
      super.writeToParcel(parcel, flags)
      parcel.writeInt(statusBar.value)
      parcel.writeInt(navigationBar.value)
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

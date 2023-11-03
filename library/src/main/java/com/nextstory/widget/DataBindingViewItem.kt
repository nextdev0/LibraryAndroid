@file:Suppress("unused")

package com.nextstory.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import com.nextstory.R
import java.lang.ref.WeakReference

/**
 * 데이터바인딩이 적용될 뷰를 지정합니다.
 *
 * 예시:
 * ```xml
 * <com.nextstory.widget.DataBindingGridLayout
 *   android:layout_width="match_parent"
 *   android:layout_height="match_parent"
 *   app:dbv_spanCount="1">
 *
 *   <com.nextstory.widget.DataBindingViewItem
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     app:dbv_layout_span="1"
 *     app:dbv_items="@{activity.testItems}"
 *     app:dbv_layout="@layout/item_layout_id"
 *     app:dbv_onItemCallback="@{(v, item, position) -> activity.onCallback(position)}"/>
 *
 * </com.nextstory.widget.DataBindingGridLayout>
 * ```
 */
class DataBindingViewItem : FrameLayout {
  internal val layoutRes: Int
  internal var owner: WeakReference<View>? = null
  internal var items: List<*>? = null
  internal var callback: DataBindingItemCallback? = null

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    setPadding(0, 0, 0, 0)

    if (attrs != null) {
      val a = context.obtainStyledAttributes(attrs, R.styleable.DataBindingViewItem)
      layoutRes = a.getResourceId(R.styleable.DataBindingViewItem_dbv_layout, NO_ID)
      a.recycle()
    } else {
      layoutRes = NO_ID
    }

    if (isInEditMode && layoutRes != NO_ID && childCount == 0) {
      val child = View.inflate(context, layoutRes, null)
      addView(child, LayoutParams(-1, -1))
    }
  }

  override fun draw(canvas: Canvas) {
    if (isInEditMode) {
      super.draw(canvas)
    }
  }

  override fun dispatchDraw(canvas: Canvas) {
    if (isInEditMode) {
      super.dispatchDraw(canvas)
    }
  }

  override fun onDraw(canvas: Canvas) {
    if (isInEditMode) {
      super.onDraw(canvas)
    }
  }

  override fun getPaddingTop(): Int {
    return 0
  }

  override fun getPaddingBottom(): Int {
    return 0
  }

  override fun getPaddingLeft(): Int {
    return 0
  }

  override fun getPaddingStart(): Int {
    return 0
  }

  override fun getPaddingRight(): Int {
    return 0
  }

  override fun getPaddingEnd(): Int {
    return 0
  }

  override fun getLeftPaddingOffset(): Int {
    return 0
  }

  override fun getTopPaddingOffset(): Int {
    return 0
  }

  override fun getRightPaddingOffset(): Int {
    return 0
  }

  override fun getBottomPaddingOffset(): Int {
    return 0
  }

  override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
    super.setPadding(0, 0, 0, 0)
  }

  override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
    super.setPaddingRelative(0, 0, 0, 0)
  }
}

object DataBindingViewItemBindingAdapter {
  @JvmStatic
  @SuppressLint("NotifyDataSetChanged")
  @BindingAdapter("dbv_items")
  fun setItems(view: DataBindingViewItem, items: List<*>) {
    view.items = items

    val owner = view.owner?.get()
    if (owner != null) {
      if (owner is DataBindingGridLayout) {
        owner.adapter?.notifyDataSetChanged()
      }

      if (owner is DataBindingViewPager) {
        owner.adapter?.notifyDataSetChanged()
      }
    }
  }

  @JvmStatic
  @BindingAdapter("dbv_onItemCallback")
  fun setCallback(view: DataBindingViewItem, callback: DataBindingItemCallback) {
    view.callback = callback
  }
}

interface DataBindingItemCallback {
  /**
   * 콜백
   *
   * @param view     상호 작용 관련 뷰
   * @param item     항목
   * @param position 항목의 인덱스
   */
  fun onDataBindingItemCallback(view: View, item: Any?, position: Int)
}

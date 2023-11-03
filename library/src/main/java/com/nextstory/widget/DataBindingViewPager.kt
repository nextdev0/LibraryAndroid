@file:Suppress(
  "unused",
  "SpellCheckingInspection",
  "MemberVisibilityCanBePrivate",
  "RedundantModalityModifier",
  "NotifyDataSetChanged",
  "RedundantGetter",
)

package com.nextstory.widget

import android.R.attr.value
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nextstory.BR
import com.nextstory.R
import java.lang.ref.WeakReference

/**
 * 데이터바인딩을 지원하는 [ViewPager]
 *
 * 예시:
 * ```xml
 * <com.nextstory.widget.DataBindingViewPager
 *   android:layout_width="match_parent"
 *   android:layout_height="match_parent"
 *   app:dbv_useInfiniteScroll="1">
 *
 *   <TextView
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:text="hello" />
 *
 *   <com.nextstory.widget.DataBindingGridLayoutItem
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:dbv_items="@{activity.testItems}"
 *     app:dbv_layout="@layout/item_layout_id"
 *     app:dbv_onItemCallback="@{(v, item, position) -> activity.onCallback(position)}"/>
 *
 * </com.nextstory.widget.DataBindingGridLayout>
 * ```
 */
class DataBindingViewPager : ViewPager {
  val useInfiniteScroll: Boolean
  var position: Int
    get() = DataBindingViewPagerBindingAdapter.getPosition(this)
    set(value) = DataBindingViewPagerBindingAdapter.setPosition(this, value)

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    if (attrs == null) {
      useInfiniteScroll = false
      position = 0
    } else {
      val a = context.obtainStyledAttributes(attrs, R.styleable.DataBindingViewPager)
      useInfiniteScroll = a.getBoolean(
        R.styleable.DataBindingViewPager_dbv_useInfiniteScroll,
        false,
      )
      position = a.getInt(
        R.styleable.DataBindingViewPager_dbv_position,
        0,
      )
      a.recycle()
    }
  }

  final override fun getAdapter(): PagerAdapter? {
    return super.getAdapter()
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  final override fun setAdapter(adapter: PagerAdapter?) {
    // NOOP: 내부 구현
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    if (adapter == null) {
      val newAdapter = InternalDataBindingPagerAdapter()
      newAdapter.useInfiniteScroll = useInfiniteScroll

      for (i in 0 until childCount) {
        val childView = getChildAt(i)
        if (childView is DataBindingViewItem) {
          childView.owner = WeakReference(this)
        }
        newAdapter.views.add(childView)
      }

      removeAllViews()
      super.setAdapter(newAdapter)

      newAdapter.registerDataSetObserver(object : DataSetObserver() {
        override fun onChanged() {
          post {
            val remain = if (useInfiniteScroll) {
              10000 * newAdapter.getRealCount()
            } else {
              0
            }
            setCurrentItem(remain, false)
          }
        }
      })

      post {
        if (useInfiniteScroll) {
          setCurrentItem(10000 * newAdapter.getRealCount() + position, false)
        } else {
          setCurrentItem(position, false)
        }
      }
    }
  }
}

internal class InternalDataBindingPagerAdapter : PagerAdapter() {
  var useInfiniteScroll = false
  val views = mutableListOf<View>()

  override fun getItemPosition(obj: Any): Int {
    return POSITION_NONE
  }

  override fun isViewFromObject(view: View, obj: Any): Boolean {
    return view == obj
  }

  override fun getCount(): Int {
    return if (useInfiniteScroll) {
      if (getRealCount() == 0) {
        0
      } else {
        Int.MAX_VALUE
      }
    } else {
      getRealCount()
    }
  }

  fun getRealCount(): Int {
    var itemCount = 0
    for (view in views) {
      if (view is DataBindingViewItem && view.items != null) {
        itemCount += view.items!!.size
      } else {
        itemCount++
      }
    }
    return itemCount
  }

  fun getItemViewType(position: Int): Int {
    var currentPosition = 0
    for (type in views.indices) {
      val view = views[type]
      val itemCount = if (view is DataBindingViewItem && view.items != null) {
        view.items!!.size
      } else {
        1
      }

      for (i in 0 until itemCount) {
        if (currentPosition == position) {
          return type
        }
        currentPosition++
      }
    }
    return 0
  }

  fun getItemId(position: Int): Long {
    val realPosition = getRealPosition(position)
    val viewType = getItemViewType(position)
    val view = views[viewType]
    return if (view is DataBindingViewItem && view.items != null) {
      view.items!![realPosition].hashCode().toLong()
    } else {
      view.hashCode().toLong()
    }
  }

  fun getRealPosition(position: Int): Int {
    var currentPosition = 0
    for (type in views.indices) {
      val view = views[type]
      val itemCount = if (view is DataBindingViewItem && view.items != null) {
        view.items!!.size
      } else {
        1
      }

      for (i in 0 until itemCount) {
        if (currentPosition == position) {
          return i
        }
        currentPosition++
      }
    }
    return currentPosition
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val realPosition = position % getRealCount()
    val viewType = getItemViewType(realPosition)
    val view = views[viewType]

    if (view is DataBindingViewItem) {
      if (view.isInEditMode) {
        container.addView(view)
        return view
      }

      val b: ViewDataBinding = DataBindingUtil.inflate(
        LayoutInflater.from(container.context),
        view.layoutRes,
        null,
        false,
      )
      container.addView(b.root)

      val itemPosition = getRealPosition(realPosition)
      val itemView = views[viewType] as DataBindingViewItem
      if (itemView.items == null || itemView.items!!.size < itemPosition) {
        return b.root
      }

      b.setVariable(BR.item, itemView.items!![itemPosition])
      b.setVariable(BR.position, itemPosition)
      b.setVariable(BR.callback, object : DataBindingItemCallback {
        override fun onDataBindingItemCallback(view: View, item: Any?, position: Int) {
          if (itemView.callback != null) {
            itemView.callback!!.onDataBindingItemCallback(view, item, position)
            return
          }

          for (v in views) {
            if (v is DataBindingViewItem) {
              if (v.callback != null) {
                v.callback!!.onDataBindingItemCallback(view, item, position)
              }
            }
          }
        }
      })

      return b.root
    }

    val viewParent = view.parent
    if (viewParent != null) {
      if (viewParent is ViewGroup) {
        viewParent.removeView(view)
      }
    }

    val newView = FrameLayout(container.context)
    newView.addView(view)
    container.addView(newView)

    return newView
  }

  override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
    if (obj is View) {
      container.removeView(obj)
    }
  }
}

object DataBindingViewPagerBindingAdapter {
  @JvmStatic
  @BindingAdapter("dbv_position")
  fun setPosition(v: DataBindingViewPager, position: Int) {
    if (v.useInfiniteScroll && v.adapter != null) {
      val realCount = (v.adapter as InternalDataBindingPagerAdapter).getRealCount()
      if (realCount > 0) {
        val currentPages = v.currentItem / realCount
        val realPosition = currentPages * realCount + position
        v.setCurrentItem(realPosition, true)
      } else {
        v.setCurrentItem(position, true)
      }
    } else {
      v.setCurrentItem(position, true)
    }
  }

  @JvmStatic
  @InverseBindingAdapter(attribute = "dbv_position", event = "dbv_positionAttrChangedAttrChanged")
  fun getPosition(v: DataBindingViewPager): Int {
    if (v.adapter != null) {
      return v.currentItem % (v.adapter as InternalDataBindingPagerAdapter).getRealCount()
    }
    return v.currentItem
  }

  @JvmStatic
  @BindingAdapter("dbv_positionAttrChangedAttrChanged")
  fun getPosition(v: DataBindingViewPager, attrChanged: InverseBindingListener) {
    v.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
      override fun onPageSelected(position: Int) {
        attrChanged.onChange()
      }
    })
  }
}

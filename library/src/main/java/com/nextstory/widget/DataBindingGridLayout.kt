@file:Suppress(
  "unused",
  "SpellCheckingInspection",
  "MemberVisibilityCanBePrivate",
  "RedundantModalityModifier",
  "NotifyDataSetChanged",
  "RedundantGetter",
)

package com.nextstory.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextstory.BR
import com.nextstory.R
import java.lang.ref.WeakReference

/**
 * 데이터바인딩을 지원하는 그리드 형식의 레이아웃
 *
 * 'app:spanCount' 속성으로 레이아웃의 열, 행 개수를 지정합니다. 하위 뷰에서는
 * 'app:dbv_layout_span' 속성으로 차지하는 개수를 지정할 수 있습니다.
 *
 * 예시:
 * ```xml
 * <com.nextstory.widget.DataBindingGridLayout
 *   android:layout_width="match_parent"
 *   android:layout_height="match_parent"
 *   app:dbv_spanCount="1">
 *
 *   <TextView
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:text="hello"
 *     app:dbv_layout_span="1" />
 *
 *   <com.nextstory.widget.DataBindingGridLayoutItem
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
class DataBindingGridLayout : RecyclerView {
  var orientation: Int = VERTICAL
    get() = field
    set(value) {
      field = value

      if (layoutManager != null && layoutManager is InternalLayoutManager) {
        (layoutManager as InternalLayoutManager).orientation = field
        adapter?.notifyDataSetChanged()
      }
    }

  var spanCount: Int = 1
    get() = field
    set(value) {
      field = value

      if (layoutManager != null && layoutManager is InternalLayoutManager) {
        (layoutManager as InternalLayoutManager).spanCount = field
        adapter?.notifyDataSetChanged()
      }
    }

  var reverseLayout: Boolean = false
    get() = field
    set(value) {
      field = value

      if (layoutManager != null && layoutManager is InternalLayoutManager) {
        (layoutManager as InternalLayoutManager).reverseLayout = field
        adapter?.notifyDataSetChanged()
      }
    }

  constructor(context: Context) : super(context) {
    init(context, null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, res: Int) : super(context, attrs, res) {
    init(context, attrs)
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    if (attrs == null) {
      orientation = VERTICAL
      spanCount = 1
      reverseLayout = false
    } else {
      val a = context.obtainStyledAttributes(attrs, R.styleable.DataBindingGridLayout)
      orientation = a.getInt(R.styleable.DataBindingGridLayout_dbv_orientation, VERTICAL)
      spanCount = a.getInt(R.styleable.DataBindingGridLayout_dbv_spanCount, 1)
      reverseLayout = a.getBoolean(R.styleable.DataBindingGridLayout_dbv_reverseLayout, false)
      a.recycle()
    }

    super.setLayoutManager(
      InternalLayoutManager(
        context,
        spanCount,
        orientation,
        reverseLayout,
      )
    )

    super.setItemAnimator(null)
    super.setHasFixedSize(false)
  }

  override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
    return p is LayoutParams
  }

  override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
    return if (orientation == VERTICAL) LayoutParams(-1, -2) else LayoutParams(-2, -1)
  }

  override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
    return LayoutParams(context, attrs)
  }

  override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
    return LayoutParams(p)
  }

  final override fun getAdapter(): Adapter<*>? {
    return super.getAdapter()
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  final override fun setAdapter(adapter: Adapter<*>?) {
    // NOOP: 내부 구현
  }

  final override fun getLayoutManager(): LayoutManager? {
    return super.getLayoutManager()
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  final override fun setLayoutManager(layout: LayoutManager?) {
    // NOOP: 내부 구현
  }

  final override fun hasFixedSize(): Boolean {
    return false
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  final override fun setHasFixedSize(hasFixedSize: Boolean) {
    // NOOP: 내부 구현
  }

  @CallSuper
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    if (adapter == null) {
      val newAdapter = InternalDataBindingGridLayoutAdapter()

      for (i in 0 until childCount) {
        val childView = getChildAt(i)
        if (childView is DataBindingViewItem) {
          childView.owner = WeakReference(this)
        }
        newAdapter.views.add(childView)
      }

      removeAllViews()
      super.setAdapter(newAdapter)
    }
  }

  @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
  class LayoutParams : GridLayoutManager.LayoutParams {
    var span: Int = 1

    constructor(width: Int, height: Int) : super(width, height)
    constructor(source: ViewGroup.LayoutParams?) : super(source)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
      if (attrs != null) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DataBindingGridLayout_Layout)
        span = a.getInt(R.styleable.DataBindingGridLayout_Layout_dbv_layout_span, 1)
        a.recycle()
      }
    }

    override fun viewNeedsUpdate(): Boolean {
      return try {
        super.viewNeedsUpdate()
      } catch (ignored: Exception) {
        false
      }
    }

    override fun isViewInvalid(): Boolean {
      return try {
        super.isViewInvalid()
      } catch (ignored: Exception) {
        false
      }
    }

    override fun isItemRemoved(): Boolean {
      return try {
        super.isItemRemoved()
      } catch (ignored: Exception) {
        false
      }
    }

    override fun isItemChanged(): Boolean {
      return try {
        super.isItemRemoved()
      } catch (ignored: Exception) {
        false
      }
    }

    override fun getViewPosition(): Int {
      return try {
        super.getViewPosition()
      } catch (ignored: Exception) {
        0
      }
    }

    override fun getViewLayoutPosition(): Int {
      return try {
        super.getViewLayoutPosition()
      } catch (ignored: Exception) {
        0
      }
    }

    override fun getViewAdapterPosition(): Int {
      return try {
        super.getViewAdapterPosition()
      } catch (ignored: Exception) {
        0
      }
    }

    override fun getSpanSize(): Int {
      return span
    }
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
class InternalLayoutManager(
  context: Context?,
  spanCount: Int,
  orientation: Int,
  reverseLayout: Boolean,
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
  override fun supportsPredictiveItemAnimations(): Boolean {
    return false
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
    try {
      super.onLayoutChildren(recycler, state)
    } catch (_: IndexOutOfBoundsException) {
      // NOOP: ignored
    }
  }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
class InternalDataBindingGridLayoutAdapter
  : RecyclerView.Adapter<InternalDataBindingGridLayoutAdapter.InternalViewHolder>() {
  val views = mutableListOf<View>()

  init {
    setHasStableIds(true)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    if (recyclerView.layoutManager is InternalLayoutManager) {
      val manager = recyclerView.layoutManager as InternalLayoutManager
      manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          if (views.size == 0) {
            return 1
          }

          val view = views[getItemViewType(position)]
          if (view.layoutParams == null
            || view.layoutParams !is DataBindingGridLayout.LayoutParams
          ) {
            return 1
          }

          val params = view.layoutParams as DataBindingGridLayout.LayoutParams
          return params.span
        }
      }
    }
  }

  override fun getItemCount(): Int {
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

  override fun getItemViewType(position: Int): Int {
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

  override fun getItemId(position: Int): Long {
    val realPosition = getRealPosition(position)
    val viewType = getItemViewType(position)
    val view = views[viewType]
    return if (view is DataBindingViewItem && view.items != null) {
      view.items!![realPosition].hashCode().toLong()
    } else {
      view.hashCode().toLong()
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternalViewHolder {
    val view = views[viewType]

    if (view is DataBindingViewItem) {
      val params = view.layoutParams as DataBindingGridLayout.LayoutParams
      params.leftMargin = 0
      params.topMargin = 0
      params.rightMargin = 0
      params.bottomMargin = 0

      if (view.isInEditMode) {
        return InternalViewHolder(view)
      }

      val b: ViewDataBinding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        view.layoutRes,
        null,
        false,
      )

      // NOTE: 데이터바인딩을 이용하는 뷰는 비정상적으로 보여지는 현상이 있어 잠시 숨깁니다.
      b.root.visibility = RecyclerView.GONE

      return InternalViewHolder(b)
    }

    val viewParent = view.parent
    if (viewParent != null) {
      if (viewParent is ViewGroup) {
        viewParent.removeView(view)
      }
    }

    return InternalViewHolder(view)
  }

  override fun onBindViewHolder(holder: InternalViewHolder, position: Int) {
    if (holder.itemView.isInEditMode) {
      return
    }

    if (holder.binding == null) {
      return
    }

    val viewType = getItemViewType(position)
    if (views[viewType] !is DataBindingViewItem) {
      return
    }

    val realPosition = getRealPosition(position)
    val gridItem = views[viewType] as DataBindingViewItem
    if (gridItem.items == null || gridItem.items!!.size < realPosition) {
      return
    }

    // NOTE: 임시로 숨겨졌던 뷰를 다시 표시합니다.
    holder.binding.root.post { holder.binding.root.visibility = RecyclerView.VISIBLE }

    holder.binding.setVariable(BR.item, gridItem.items!![realPosition])
    holder.binding.setVariable(BR.position, realPosition)
    holder.binding.setVariable(BR.callback, object : DataBindingItemCallback {
      override fun onDataBindingItemCallback(view: View, item: Any?, position: Int) {
        if (gridItem.callback != null) {
          gridItem.callback!!.onDataBindingItemCallback(view, item, position)
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
  }

  private fun getRealPosition(position: Int): Int {
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

  class InternalViewHolder : RecyclerView.ViewHolder {
    val binding: ViewDataBinding?

    constructor(itemView: View) : super(wrapView(itemView)) {
      this.binding = null
    }

    constructor(binding: ViewDataBinding) : super(binding.root) {
      this.binding = binding
    }

    companion object {
      fun wrapView(view: View): View {
        return view
      }
    }
  }
}

object DataBindingGridLayoutBindingAdapter {
  @JvmStatic
  @BindingAdapter("dbv_spanCount")
  fun setSpanCount(view: DataBindingGridLayout, count: Int) {
    if (view.layoutManager != null && view.layoutManager is InternalLayoutManager && count > 0) {
      (view.layoutManager as InternalLayoutManager).spanCount = count
    }
  }

  @JvmStatic
  @BindingAdapter("dbv_layout_span")
  fun setSpanCount(view: View, count: Int) {
    if (view.layoutParams != null && view.layoutParams is DataBindingGridLayout.LayoutParams) {
      (view.layoutParams as DataBindingGridLayout.LayoutParams).span = count
      view.requestLayout()
    }
  }
}

@file:Suppress("unused", "DiscouragedApi", "InternalInsetResource", "NOTHING_TO_INLINE")

package com.nextstory.util

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nextstory.app.globalContext

/**
 * dp 사이즈 변환
 */
val Int.dp: Int
  get() {
    return (globalContext.resources.displayMetrics.density * this).toInt()
  }

/**
 * dp 사이즈 변환
 */
val Float.dp: Int
  get() {
    return (globalContext.resources.displayMetrics.density * this).toInt()
  }

/**
 * 화면 밀도값 반환
 */
val View?.dpi: Float
  get() {
    if (this == null) {
      return 1f
    }
    return resources.displayMetrics.density
  }

/**
 * 상태바 사이즈 반환
 */
fun View?.getStatusBarHeight(): Int {
  if (this == null) {
    return 0
  }

  if (isInEditMode) {
    return (dpi * 24).toInt()
  }

  var size = 0
  val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    size = resources.getDimensionPixelSize(resourceId)
  }
  return size
}

/**
 * 내비게이션바 사이즈 반환
 */
fun View?.getNavigationBarHeight(): Int {
  if (this == null) {
    return 0
  }

  if (isInEditMode) {
    return (dpi * 48).toInt()
  }

  var size = 0
  val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
  if (resourceId > 0) {
    size = resources.getDimensionPixelSize(resourceId)
  }
  return size
}

/**
 * 제스처바 유무 반환
 */
fun View?.hasGestureBar(): Boolean {
  if (this == null || isInEditMode) {
    return false
  }

  val res = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
  if (res == 0) {
    return false
  }

  return resources.getInteger(res) == 2
}

/**
 * [Activity.setResult], [Activity.finish]의 보일러 플레이트가 구현된 메소드
 */
inline fun Activity?.finishWithResult(block: (Intent) -> Unit) {
  if (this != null) {
    val intent = Intent()
    block(intent)
    setResult(AppCompatActivity.RESULT_OK, intent)
    finish()
  }
}

/**
 * 삼항연산자 대체
 */
inline fun <T> Boolean.takeIfOrElse(trueValue: T, falseValue: T): T {
  return if (this) trueValue else falseValue
}

interface Action

interface Action0 : Action {
  operator fun invoke()
}

interface Action1<in P1> : Action {
  operator fun invoke(p1: P1)
}

interface Action2<in P1, in P2> : Action {
  operator fun invoke(p1: P1, p2: P2)
}

interface Action3<in P1, in P2, in P3> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3)
}

interface Action4<in P1, in P2, in P3, in P4> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4)
}

interface Action5<in P1, in P2, in P3, in P4, in P5> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5)
}

interface Action6<in P1, in P2, in P3, in P4, in P5, in P6> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6)
}

interface Action7<in P1, in P2, in P3, in P4, in P5, in P6, in P7> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7)
}

interface Action8<in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8)
}

interface Action9<in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8, in P9> : Action {
  operator fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9)
}

interface Action10<in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8, in P9, in P10> : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10
  )
}

interface Action11<in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8, in P9, in P10, in P11> :
  Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11
  )
}

interface Action12<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12
  )
}

interface Action13<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13
  )
}

interface Action14<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14
  )
}

interface Action15<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15
  )
}

interface Action16<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16
  )
}

interface Action17<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17
  )
}

interface Action18<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17, in P18
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17,
    p18: P18
  )
}

interface Action19<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17, in P18, in P19
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17,
    p18: P18,
    p19: P19
  )
}

interface Action20<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17, in P18, in P19, in P20
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17,
    p18: P18,
    p19: P19,
    p20: P20
  )
}

interface Action21<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17, in P18, in P19, in P20, in P21
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17,
    p18: P18,
    p19: P19,
    p20: P20,
    p21: P21
  )
}

interface Action22<
  in P1, in P2, in P3, in P4, in P5, in P6, in P7, in P8,
  in P9, in P10, in P11, in P12, in P13, in P14, in P15, in P16,
  in P17, in P18, in P19, in P20, in P21, in P22
  > : Action {
  operator fun invoke(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16,
    p17: P17,
    p18: P18,
    p19: P19,
    p20: P20,
    p21: P21,
    p22: P22
  )
}

interface ActionN : Action {
  operator fun invoke(vararg args: Any?)
}

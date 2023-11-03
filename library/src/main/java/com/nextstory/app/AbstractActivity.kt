@file:Suppress("unused")

package com.nextstory.app

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.nextstory.R
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

/**
 * 기본 기능이 포함된 [AppCompatActivity]
 *
 * 포함된 항목:
 * * 포커스를 잃은 상태에서 빈 영역 탭했을때 키보드를 닫음
 * * 앱 현지화를 위한 리소스 처리
 * * 앱 테마 변경
 */
abstract class AbstractActivity : AppCompatActivity() {
  private val actualDeviceHeight = AtomicInteger(0)

  private var localizationContext: Context? = null
  private var currentLocaleState: Locale? = null
  private var currentThemeState: AppTheme? = null
  private var currentDarkMode = false

  private var inputMethodManager: InputMethodManager? = null
  private val touchPoint = PointF(0f, 0f)
  private val editTextRect = Rect()
  private var focused = false

  override fun attachBaseContext(newBase: Context) {
    val localizationNewBase = getLocalizationContextWrapper(newBase, newBase.resources)
    super.attachBaseContext(localizationNewBase)
  }

  override fun getResources(): Resources {
    if (localizationContext == null) {
      localizationContext = getLocalizationContextWrapper(this, super.getResources())
    }
    return localizationContext!!.resources
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
      val insetsController = WindowCompat.getInsetsController(window, window.decorView)
      insetsController.isAppearanceLightNavigationBars = false
    }

    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      window.isNavigationBarContrastEnforced = false
      window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    currentDarkMode = resources.getBoolean(R.bool.is_darkMode)
    currentThemeState = currentTheme
    currentLocaleState = currentLocale
  }

  override fun onResume() {
    super.onResume()
    checkConfiguration()
  }

  override fun onDestroy() {
    localizationContext = null
    super.onDestroy()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    checkConfiguration()
  }

  private fun checkConfiguration() {
    val newDarkMode = resources.getBoolean(R.bool.is_darkMode)
    if (currentThemeState != currentTheme ||
      (currentThemeState == AppTheme.System && currentDarkMode != newDarkMode)
    ) {
      currentDarkMode = resources.getBoolean(R.bool.is_darkMode)
      currentThemeState = currentTheme
      recreate()
    }

    if (currentLocaleState != currentLocale) {
      currentLocaleState = currentLocale
      localizationContext = null
      recreate()
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    val view = currentFocus
    if (view != null && ev != null) {
      if (ev.action == MotionEvent.ACTION_DOWN) {
        touchPoint.set(ev.x, ev.y)
        return super.dispatchTouchEvent(ev)
      }

      if (ev.action != MotionEvent.ACTION_UP) {
        return super.dispatchTouchEvent(ev)
      }

      if (view is EditText) {
        // 스크롤 인식 체크
        val distance = (view.resources.displayMetrics.density * 24).toInt()
        if (abs(ev.x - touchPoint.x) > distance || abs(ev.y - touchPoint.y) > distance) {
          return super.dispatchTouchEvent(ev)
        }

        view.getGlobalVisibleRect(editTextRect)

        // 포커스된 뷰 위치 체크
        if (ev.x >= editTextRect.left && ev.x <= editTextRect.right
          && ev.y >= editTextRect.top && ev.y <= editTextRect.bottom
        ) {
          return super.dispatchTouchEvent(ev)
        }

        view.viewTreeObserver.addOnGlobalFocusChangeListener(
          object : ViewTreeObserver.OnGlobalFocusChangeListener {
            override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
              view.viewTreeObserver.removeOnGlobalFocusChangeListener(this)
              if (newFocus != null) {
                focused = true
              }
            }
          })

        mainThreadHandler.postDelayed({
          if (inputMethodManager == null) {
            inputMethodManager = getSystemService(InputMethodManager::class.java)
          }

          if (!focused && inputMethodManager != null) {
            inputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
          }
          focused = false
        }, 70L)
      }
    }

    return super.dispatchTouchEvent(ev)
  }
}

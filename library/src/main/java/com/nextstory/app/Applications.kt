@file:Suppress(
  "unused",
  "DEPRECATION",
  "StaticFieldLeak",
  "SpellCheckingInspection",
)

package com.nextstory.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.Locale

private var currentApplicationImpl: Application? = null
private var globalContextImpl: Context? = null

/**
 * 메인스레드 [Handler]
 */
val mainThreadHandler = Handler(Looper.getMainLooper())

/**
 * 현재 상위에서 실행중인 액티비티
 */
val foregroundActivity: Activity
  get() {
    val activityThread = ActivityThreads.currentActivityThreadMethod(null)
    val activities = ActivityThreads.mActivitiesField.get(activityThread) as ArrayMap<*, *>
    for (e in activities.values) {
      if (!ActivityThreads.getPausedField(e).getBoolean(e)) {
        return ActivityThreads.getActivityField(e).get(e) as Activity
      }
    }
    throw UnsupportedOperationException()
  }

/**
 * [Application]
 */
val currentApplication: Application
  @SuppressLint("PrivateApi")
  get() {
    if (currentApplicationImpl == null) {
      currentApplicationImpl = ActivityThreads.currentApplicationMethod(null) as Application
    }
    return currentApplicationImpl!!
  }

/**
 * 전역 [Context]
 */
val globalContext: Context
  get() {
    if (globalContextImpl == null) {
      val context = currentApplication
      globalContextImpl = getLocalizationContextWrapper(context, context.resources)
    }
    return globalContextImpl!!
  }

/**
 * 현재 테마
 */
var currentTheme: AppTheme
  get() {
    var theme = PreferenceImpl.getTheme()
    if (theme == null) {
      theme = when (AppCompatDelegate.getDefaultNightMode()) {
        AppCompatDelegate.MODE_NIGHT_AUTO_TIME, AppCompatDelegate.MODE_NIGHT_NO -> AppTheme.Light
        AppCompatDelegate.MODE_NIGHT_YES -> AppTheme.Dark
        else -> AppTheme.System
      }
      PreferenceImpl.setTheme(theme)
    }

    AppCompatDelegate.setDefaultNightMode(
      when (theme) {
        AppTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
        AppTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
      }
    )

    return theme
  }
  set(value) {
    PreferenceImpl.setTheme(value)
    AppCompatDelegate.setDefaultNightMode(
      when (value) {
        AppTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
        AppTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
      }
    )
  }

/**
 * 현재 설정된 [Locale]
 */
var currentLocale: Locale
  get() = PreferenceImpl.getLocale()
  set(value) {
    PreferenceImpl.setLocale(value)

    val context = currentApplication
    globalContextImpl = getLocalizationContextWrapper(context, context.resources)

    foregroundActivity.recreate()
  }

/**
 * 전달한 [Context]를 지역화 설정이 적용된 [Context]로 새로 만들어 반환합니다.
 */
fun getLocalizationContextWrapper(context: Context, res: Resources): Context {
  Locale.getAvailableLocales()

  val locale = currentLocale
  val configuration = Configuration(res.configuration)
  Locale.setDefault(locale)
  configuration.locale = locale
  configuration.setLocale(locale)

  val localeList = LocaleListCompat.create(locale)
  ConfigurationCompat.setLocales(configuration, localeList)

  return context.createConfigurationContext(configuration)
}

/**
 * 기본 토스트를 표시합니다.
 *
 * [Toast] 간소화 버전
 */
fun showToast(msg: String) {
  Toast.makeText(globalContext, msg, Toast.LENGTH_SHORT).show()
}

/**
 * 기본 토스트를 표시합니다.
 *
 * [Toast] 간소화 버전
 */
fun showToast(@StringRes msgRes: Int) {
  Toast.makeText(globalContext, msgRes, Toast.LENGTH_SHORT).show()
}

/**
 * 기본 토스트를 대기 시간이 길게 표시합니다.
 *
 * [Toast] 간소화 버전
 */
fun showToastLong(msg: String) {
  Toast.makeText(globalContext, msg, Toast.LENGTH_LONG).show()
}

/**
 * 기본 토스트를 대기 시간이 길게 표시합니다.
 *
 * [Toast] 간소화 버전
 */
fun showToastLong(@StringRes msgRes: Int) {
  Toast.makeText(globalContext, msgRes, Toast.LENGTH_LONG).show()
}

enum class AppTheme {
  System,
  Light,
  Dark,
}

private object PreferenceImpl {
  private var pref: SharedPreferences? = null

  fun getLocalizationPref(): SharedPreferences {
    if (pref == null) {
      pref = currentApplication.getSharedPreferences("app", Context.MODE_PRIVATE)
    }
    return pref!!
  }

  fun getTheme(): AppTheme? {
    val src = getLocalizationPref().getString("theme", null) ?: return null
    return AppTheme.valueOf(src)
  }

  fun setTheme(theme: AppTheme) {
    getLocalizationPref()
      .edit()
      .putString("theme", theme.name)
      .apply()
  }

  fun getLocale(): Locale {
    val pref = getLocalizationPref()
    val language = pref.getString("language", "")
    val country = pref.getString("country", "")

    var locale: Locale? = null
    for (item in Locale.getAvailableLocales()) {
      if (item == null) {
        continue
      }

      if (item.country == country && item.language == language) {
        locale = item
        break
      }

      if (locale == null && item.language == language) {
        locale = item
        continue
      }

      if (locale == null && item.country == country) {
        locale = item
        continue
      }
    }

    if (locale != null) {
      return locale
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      currentApplication.resources.configuration.locales.get(0)
    } else {
      currentApplication.resources.configuration.locale
    }
  }

  fun setLocale(locale: Locale) {
    val sameLanguages: MutableList<Locale> = ArrayList()
    var isSupportedLocale = false
    for (item in Locale.getAvailableLocales()) {
      if (item.language == locale.language) {
        sameLanguages.add(item)
      }
      if (item.country == locale.language) {
        isSupportedLocale = true
        break
      }
    }

    if (!isSupportedLocale && sameLanguages.size > 0) {
      val firstSameLocale = sameLanguages[0]
      getLocalizationPref()
        .edit()
        .putString("language", firstSameLocale.language)
        .putString("country", firstSameLocale.country)
        .apply()
    }
  }
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
private object ActivityThreads {
  private var _activityThreadClass: Class<*>? = null
  val activityThreadClass: Class<*>
    get() = _activityThreadClass ?: Class.forName("android.app.ActivityThread")
      .also { _activityThreadClass = it }

  private var _currentApplicationMethod: Method? = null
  val currentApplicationMethod: Method
    get() = _currentApplicationMethod ?: activityThreadClass.getMethod("currentApplication")
      .also { _currentApplicationMethod = it }

  private var _currentActivityThreadMethod: Method? = null
  val currentActivityThreadMethod: Method
    get() = _currentActivityThreadMethod ?: activityThreadClass.getMethod("currentActivityThread")
      .also { _currentActivityThreadMethod = it }

  private var _mActivitiesField: Field? = null
  val mActivitiesField: Field
    get() = _mActivitiesField ?: activityThreadClass.getDeclaredField("mActivities")
      .also {
        _mActivitiesField = it
        _mActivitiesField!!.isAccessible = true
      }

  private var _pausedField: Field? = null
  fun getPausedField(obj: Any): Field {
    if (_pausedField == null) {
      _pausedField = obj.javaClass.getDeclaredField("paused")
      _pausedField!!.isAccessible = true
    }
    return _pausedField!!
  }

  private var _activityField: Field? = null
  fun getActivityField(obj: Any): Field {
    if (_activityField == null) {
      _activityField = obj.javaClass.getDeclaredField("activity")
      _activityField!!.isAccessible = true
    }
    return _activityField!!
  }
}

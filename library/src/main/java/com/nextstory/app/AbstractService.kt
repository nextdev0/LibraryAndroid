@file:Suppress("unused")

package com.nextstory.app

import android.app.Service
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * 기본 기능이 포함된 [Service]
 *
 * 포함된 항목:
 * * 앱 현지화를 위한 리소스 처리
 */
abstract class AbstractService : Service() {
  private var localizationContext: Context? = null
  private var currentLocaleState: Locale? = null

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

  override fun onCreate() {
    currentLocaleState = currentLocale
  }

  override fun onDestroy() {
    localizationContext = null
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    checkConfiguration()
  }

  private fun checkConfiguration() {
    if (currentLocaleState != currentLocale) {
      currentLocaleState = currentLocale
      localizationContext = null
    }
  }
}

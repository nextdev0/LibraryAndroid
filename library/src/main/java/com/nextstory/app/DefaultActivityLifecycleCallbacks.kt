@file:Suppress("unused")

package com.nextstory.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 기본 구현이 포함된 [Application.ActivityLifecycleCallbacks]
 */
interface DefaultActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    // NOOP: ignored
  }

  override fun onActivityStarted(activity: Activity) {
    // NOOP: ignored
  }

  override fun onActivityResumed(activity: Activity) {
    // NOOP: ignored
  }

  override fun onActivityPaused(activity: Activity) {
    // NOOP: ignored
  }

  override fun onActivityStopped(activity: Activity) {
    // NOOP: ignored
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    // NOOP: ignored
  }

  override fun onActivityDestroyed(activity: Activity) {
    // NOOP: ignored
  }
}

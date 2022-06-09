package com.alpha0010.settings

import android.view.Choreographer
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class RnNativeSettingsViewManager : SimpleViewManager<View>() {
  override fun getName() = "RnNativeSettingsView"

  override fun createViewInstance(reactContext: ThemedReactContext): View {
    return FragmentContainerView(reactContext)
  }

  @ReactProp(name = "config")
  fun setConfig(view: View, config: String) {
    val fm = getFragmentManager(view) ?: return
    fm.beginTransaction()
      .replace(view.id, SettingsFragment(MemoryDataStore { layoutChildren(view) }))
      .commit()
  }

  private fun getFragmentManager(view: View): FragmentManager? {
    val context = view.context
    if (context !is ThemedReactContext) {
      return null
    }
    val activity = context.currentActivity
    if (activity === null || activity !is FragmentActivity) {
      return null
    }
    return activity.supportFragmentManager
  }

  private fun layoutChildren(view: View) {
    Choreographer.getInstance().postFrameCallback {
      view.measure(
        View.MeasureSpec.makeMeasureSpec(view.measuredWidth, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(view.measuredHeight, View.MeasureSpec.EXACTLY)
      )
      view.layout(view.left, view.top, view.right, view.bottom)
    }
  }
}

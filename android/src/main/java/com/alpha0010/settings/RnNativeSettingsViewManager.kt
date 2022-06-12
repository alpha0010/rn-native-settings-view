package com.alpha0010.settings

import android.view.Choreographer
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

class RnNativeSettingsViewManager : SimpleViewManager<View>() {
  override fun getName() = "RnNativeSettingsView"

  override fun createViewInstance(reactContext: ThemedReactContext): View {
    return FragmentContainerView(reactContext)
  }

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
    return mapOf(
      "topChange" to mapOf(
        "phasedRegistrationNames" to mapOf("bubbled" to "onChange")
      )
    )
  }

  @ReactProp(name = "config")
  fun setConfig(view: View, config: String) {
    val fm = getFragmentManager(view) ?: return
    fm.beginTransaction()
      .replace(view.id, SettingsFragment(MemoryDataStore {
        layoutChildren(view)
        dispatchEvent(view, it)
      }))
      .commit()
  }

  private fun dispatchEvent(view: View, event: WritableMap) {
    val context = view.context
    if (context !is ThemedReactContext) {
      return
    }
    context.getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(view.id, "topChange", event)
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

  /**
   * Force render at proper dimensions. UI fails to update on settings
   * change without this workaround.
   */
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

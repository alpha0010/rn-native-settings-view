package com.alpha0010.settings

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
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
      ),
      "topDetails" to mapOf(
        "phasedRegistrationNames" to mapOf("bubbled" to "onDetails")
      )
    )
  }

  @ReactProp(name = "config")
  fun setConfig(view: View, config: ReadableMap) {
    val dataStore = MemoryDataStore {
      view.post { layoutChildren(view) }
      val event = Arguments.createMap()
      event.putMap("data", it)
      dispatchEvent(view, "topChange", event)
    }
    val fragment = SettingsFragment(config, dataStore) {
      val event = Arguments.createMap()
      event.putString("data", it)
      dispatchEvent(view, "topDetails", event)
    }
    view.post {
      val fm = getFragmentManager(view) ?: return@post
      fm.beginTransaction()
        .replace(view.id, fragment)
        .commitNow()
      layoutChildren(view)
      dataStore.ready = true
    }
  }

  private fun dispatchEvent(view: View, eventName: String, event: WritableMap) {
    val context = view.context
    if (context !is ThemedReactContext) {
      return
    }
    context.getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(view.id, eventName, event)
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
    view.measure(
      View.MeasureSpec.makeMeasureSpec(view.measuredWidth, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(view.measuredHeight, View.MeasureSpec.EXACTLY)
    )
    view.layout(view.left, view.top, view.right, view.bottom)
  }
}

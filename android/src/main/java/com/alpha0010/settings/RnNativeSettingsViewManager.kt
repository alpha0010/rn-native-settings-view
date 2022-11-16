package com.alpha0010.settings

import android.view.View
import androidx.fragment.app.*
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

class RnNativeSettingsViewManager : SimpleViewManager<FragmentContainerView>() {
  override fun getName() = "RnNativeSettingsView"

  override fun createViewInstance(reactContext: ThemedReactContext): FragmentContainerView {
    val view = FragmentContainerView(reactContext)
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) = Unit
      override fun onViewDetachedFromWindow(v: View) {
        // Mark for re-initialize.
        v.getFragmentManager()?.findSettingsFragment(v)?.signature = ""
      }
    })
    return view
  }

  override fun onDropViewInstance(view: FragmentContainerView) {
    val fm = view.getFragmentManager()
    val frag = fm?.findSettingsFragment(view)
    if (frag != null) {
      fm.commitNow { remove(frag) }
    }

    super.onDropViewInstance(view)
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
  fun setConfig(view: FragmentContainerView, config: ReadableMap) {
    val fm = view.getFragmentManager() ?: return
    val oldFrag = fm.findSettingsFragment(view)
    val dataStore: MemoryDataStore
    val confP: ProcessedConfig
    if (oldFrag != null) {
      dataStore = oldFrag.dataStore
      dataStore.ready = false
      confP = processSettingsConfig(config, dataStore, view.resources)
      if (oldFrag.signature == confP.signature) {
        // Existing fragment has same structure, reuse.
        view.post {
          oldFrag.notifyDataChanged()
          layoutChildren(view)
          dataStore.ready = true
        }
        return
      }
    } else {
      dataStore = MemoryDataStore {
        view.post { layoutChildren(view) }
        val event = Arguments.createMap()
        event.putMap("data", it)
        dispatchEvent(view, "topChange", event)
      }
      confP = processSettingsConfig(config, dataStore, view.resources)
    }

    val newFrag = SettingsFragment(dataStore, confP.signature, confP.elements) {
      val event = Arguments.createMap()
      event.putString("data", it)
      dispatchEvent(view, "topDetails", event)
    }
    view.removeAllViews()
    fm.commit {
      if (oldFrag != null) {
        remove(oldFrag)
      }
      add(newFrag, getFragmentTag(view))

      runOnCommit {
        view.addView(newFrag.requireView())
        layoutChildren(view)
        dataStore.ready = true
      }
    }
  }

  private fun dispatchEvent(view: View, eventName: String, event: WritableMap) {
    val context = view.context as? ThemedReactContext ?: return
    context.getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(view.id, eventName, event)
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

/**
 * Lookup tag for fragment associated with FragmentContainerView.
 */
private fun getFragmentTag(view: View) = "SettingsFragment:" + view.id

private fun FragmentManager.findSettingsFragment(view: View): SettingsFragment? {
  return findFragmentByTag(getFragmentTag(view)) as? SettingsFragment
}

private fun View.getFragmentManager(): FragmentManager? {
  val reactContext = context as? ThemedReactContext ?: return null
  val activity = reactContext.currentActivity as? FragmentActivity
  return activity?.supportFragmentManager
}

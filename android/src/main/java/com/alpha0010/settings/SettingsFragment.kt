package com.alpha0010.settings

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.TYPE_FIRST_COLOR_INT
import android.util.TypedValue.TYPE_LAST_COLOR_INT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.annotation.ColorInt
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType

class SettingsFragment(config: ReadableMap, private val dataStore: PreferenceDataStore) :
  PreferenceFragmentCompat() {
  private val elements = mutableListOf<PreferenceElement>()

  init {
    val keys = config.keySetIterator()
    while (keys.hasNextKey()) {
      val key = keys.nextKey()
      val elData = config.getMap(key)
      when (elData?.getString("type")) {
        "list" -> {
          val title = elData.getString("title") ?: continue
          val labels = elData.getArray("labels")?.toStringList() ?: continue
          val values = elData.getArray("values")?.toStringList() ?: continue
          dataStore.putString(key, elData.getString("initialValue"))
          elements.add(ListElement(key, title, labels, values, elData.getInt("weight")))
        }
        "switch" -> {
          val title = elData.getString("title") ?: continue
          dataStore.putBoolean(key, elData.getBoolean("initialValue"))
          elements.add(SwitchElement(key, title, elData.getInt("weight")))
        }
      }
    }
    elements.sortBy { it.weight }
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    preferenceManager.preferenceDataStore = dataStore

    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    for (element in elements) {
      when (element) {
        is ListElement -> {
          val listPref = SummaryListPreference(context).apply {
            key = element.key
            title = element.title
            dialogTitle = element.title
            entries = element.labels.toTypedArray()
            entryValues = element.values.toTypedArray()
          }
          screen.addPreference(listPref)
        }
        is SwitchElement -> {
          val switchPref = SwitchPreferenceCompat(context).apply {
            key = element.key
            title = element.title
          }
          screen.addPreference(switchPref)
        }
      }
    }

    preferenceScreen = screen
  }

  override fun onCreateRecyclerView(
    inflater: LayoutInflater,
    parent: ViewGroup,
    savedInstanceState: Bundle?
  ): RecyclerView {
    val recycler = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
    recycler.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
      override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        val effect = super.createEdgeEffect(view, direction)
        effect.color = view.context.resolveColor(R.attr.colorControlHighlight, Color.BLACK)
        return effect
      }
    }
    return recycler
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val view = super.onCreateView(inflater, container, savedInstanceState)
    view.setBackgroundColor(
      view.context.resolveColor(android.R.attr.colorBackground, 0xfffafafa.toInt())
    )
    return view
  }
}

@ColorInt
fun Context.resolveColor(resid: Int, @ColorInt default: Int): Int {
  val result = TypedValue()
  theme.resolveAttribute(resid, result, true)
  return if (result.type in TYPE_FIRST_COLOR_INT..TYPE_LAST_COLOR_INT) {
    result.data
  } else {
    default
  }
}

fun ReadableArray.toStringList(): List<String> {
  val res = mutableListOf<String>()
  for (i in 0 until this.size()) {
    if (this.getType(i) == ReadableType.String) {
      res.add(this.getString(i))
    }
  }
  return res
}

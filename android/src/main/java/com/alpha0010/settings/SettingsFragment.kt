package com.alpha0010.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.ReadableMap

data class SwitchElement(
  val key: String,
  val title: String,
  val weight: Int
)

class SettingsFragment(config: ReadableMap, private val dataStore: PreferenceDataStore) :
  PreferenceFragmentCompat() {
  private val elements = mutableListOf<SwitchElement>()

  init {
    val keys = config.keySetIterator()
    while (keys.hasNextKey()) {
      val key = keys.nextKey()
      val swData = config.getMap(key)
      if (swData != null) {
        val title = swData.getString("title")
        if (title != null) {
          dataStore.putBoolean(key, swData.getBoolean("initialValue"))
          elements.add(SwitchElement(key, title, swData.getInt("weight")))
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
      val switchPreference = SwitchPreferenceCompat(context).apply {
        key = element.key
        title = element.title
      }
      screen.addPreference(switchPreference)
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
        effect.color = Color.BLACK
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
    view.setBackgroundColor(0xfffafafa.toInt())
    return view
  }
}

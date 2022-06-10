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

class SettingsFragment(private val dataStore: PreferenceDataStore) : PreferenceFragmentCompat() {
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    preferenceManager.preferenceDataStore = dataStore

    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    val switchPreference = SwitchPreferenceCompat(context).apply {
      key = "switch"
      title = "Sample toggle switch"
    }
    screen.addPreference(switchPreference)

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

package com.alpha0010.settings

import android.os.Bundle
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

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
}

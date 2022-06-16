package com.alpha0010.settings

import androidx.preference.PreferenceDataStore
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

class MemoryDataStore(private val onChange: (data: WritableMap) -> Unit) : PreferenceDataStore() {
  private val booleans = mutableMapOf<String, Boolean>()

  override fun putBoolean(key: String, value: Boolean) {
    booleans[key] = value
    onChange(serialize())
  }

  override fun getBoolean(key: String, defValue: Boolean): Boolean {
    return booleans[key] ?: defValue
  }

  private fun serialize(): WritableMap {
    val data = Arguments.createMap()
    for (entry in booleans) {
      data.putBoolean(entry.key, entry.value)
    }
    return data
  }
}

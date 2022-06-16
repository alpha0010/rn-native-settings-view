package com.alpha0010.settings

import androidx.preference.PreferenceDataStore
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

class MemoryDataStore(private val onChange: (data: WritableMap) -> Unit) : PreferenceDataStore() {
  private val booleans = mutableMapOf<String, Boolean>()
  private val strings = mutableMapOf<String, String>()

  override fun putString(key: String, value: String?) {
    if (value == null) {
      strings.remove(key)
    } else {
      strings[key] = value
    }
    dispatchUpdate()
  }

  override fun putBoolean(key: String, value: Boolean) {
    booleans[key] = value
    dispatchUpdate()
  }

  override fun getString(key: String, defValue: String?): String? {
    return strings[key] ?: defValue
  }

  override fun getBoolean(key: String, defValue: Boolean): Boolean {
    return booleans[key] ?: defValue
  }

  private fun dispatchUpdate() {
    onChange(serialize())
  }

  private fun serialize(): WritableMap {
    val data = Arguments.createMap()
    for (entry in booleans) {
      data.putBoolean(entry.key, entry.value)
    }
    for (entry in strings) {
      data.putString(entry.key, entry.value)
    }
    return data
  }
}

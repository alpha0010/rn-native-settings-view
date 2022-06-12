package com.alpha0010.settings

import androidx.preference.PreferenceDataStore
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

class MemoryDataStore(private val onChange: (data: WritableMap) -> Unit) : PreferenceDataStore() {
  private val booleans = mutableMapOf<String, Boolean>()

  override fun putString(key: String, value: String?) {
    TODO()
    onChange(serialize())
  }

  override fun putStringSet(key: String, values: Set<String>?) {
    TODO()
    onChange(serialize())
  }

  override fun putInt(key: String, value: Int) {
    TODO()
    onChange(serialize())
  }

  override fun putLong(key: String, value: Long) {
    TODO()
    onChange(serialize())
  }

  override fun putFloat(key: String, value: Float) {
    TODO()
    onChange(serialize())
  }

  override fun putBoolean(key: String, value: Boolean) {
    booleans[key] = value
    onChange(serialize())
  }

  override fun getString(key: String, defValue: String?): String? {
    return super.getString(key, defValue)
  }

  override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
    return super.getStringSet(key, defValues)
  }

  override fun getInt(key: String, defValue: Int): Int {
    return super.getInt(key, defValue)
  }

  override fun getLong(key: String, defValue: Long): Long {
    return super.getLong(key, defValue)
  }

  override fun getFloat(key: String, defValue: Float): Float {
    return super.getFloat(key, defValue)
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

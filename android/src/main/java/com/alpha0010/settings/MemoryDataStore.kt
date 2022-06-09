package com.alpha0010.settings

import androidx.preference.PreferenceDataStore

class MemoryDataStore(private val onChange: () -> Unit) : PreferenceDataStore() {
  private val booleans = mutableMapOf<String, Boolean>()

  override fun putString(key: String, value: String?) {
    onChange()
  }

  override fun putStringSet(key: String, values: Set<String>?) {
    onChange()
  }

  override fun putInt(key: String, value: Int) {
    onChange()
  }

  override fun putLong(key: String, value: Long) {
    onChange()
  }

  override fun putFloat(key: String, value: Float) {
    onChange()
  }

  override fun putBoolean(key: String, value: Boolean) {
    booleans[key] = value
    onChange()
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
}

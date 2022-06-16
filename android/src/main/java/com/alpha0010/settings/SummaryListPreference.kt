package com.alpha0010.settings

import android.content.Context
import androidx.preference.ListPreference

class SummaryListPreference(context: Context) : ListPreference(context) {
  override fun setValue(value: String?) {
    super.setValue(value)
    summary = entry
  }
}

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
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.forEach
import androidx.recyclerview.widget.RecyclerView

class SettingsFragment(
  val dataStore: MemoryDataStore,
  var signature: String,
  private val elements: List<PreferenceElement>,
  private val onDetails: (key: String) -> Unit
) :
  PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    preferenceManager.preferenceDataStore = dataStore

    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    for (element in elements) {
      when (element) {
        is DetailsElement -> {
          val detailsPref = Preference(context).apply {
            key = element.key
            title = element.title
            summary = element.details
            icon = element.icon
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
              onDetails(it.key)
              true
            }
          }
          screen.addPreference(detailsPref)
        }
        is ListElement -> {
          val listPref = SummaryListPreference(context).apply {
            key = element.key
            title = element.title
            dialogTitle = element.title
            entries = element.labels.toTypedArray()
            entryValues = element.values.toTypedArray()
            icon = element.icon
          }
          screen.addPreference(listPref)
        }
        is SwitchElement -> {
          val switchPref = SwitchPreferenceCompat(context).apply {
            key = element.key
            title = element.title
            icon = element.icon
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

  fun notifyDataChanged() {
    preferenceScreen.forEach {
      when (it) {
        is SummaryListPreference -> it.value = dataStore.getString(it.key, it.value)
        is SwitchPreferenceCompat -> it.isChecked = dataStore.getBoolean(it.key, it.isChecked)
      }
    }
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

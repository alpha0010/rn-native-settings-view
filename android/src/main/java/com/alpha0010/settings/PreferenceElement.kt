package com.alpha0010.settings

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.preference.PreferenceDataStore
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType

sealed class PreferenceElement {
  abstract val key: String
  abstract val icon: Drawable?
  abstract val weight: Int
}

data class DetailsElement(
  override val key: String,
  val title: String,
  val details: String,
  override val icon: Drawable?,
  override val weight: Int
) : PreferenceElement()

data class ListElement(
  override val key: String,
  val title: String,
  val labels: List<String>,
  val values: List<String>,
  override val icon: Drawable?,
  override val weight: Int
) : PreferenceElement()

data class SwitchElement(
  override val key: String,
  val title: String,
  override val icon: Drawable?,
  override val weight: Int
) : PreferenceElement()

data class ProcessedConfig(
  val elements: List<PreferenceElement>,
  val signature: String
)

const val ICON_KEY = "icon"

fun getIcon(data: ReadableMap, res: Resources): IconFontDrawable? {
  val icnData = if (data.hasKey(ICON_KEY) && data.getType(ICON_KEY) == ReadableType.Map) {
    data.getMap(ICON_KEY)
  } else {
    return null
  }
  val charCode = icnData?.getInt("char") ?: return null
  val font = icnData.getString("font") ?: return null
  val fg = icnData.getInt("fgP")
  val bg = icnData.getInt("bgP")
  return IconFontDrawable(charCode, font, fg, bg, res)
}

fun processSettingsConfig(
  config: ReadableMap,
  dataStore: PreferenceDataStore,
  res: Resources
): ProcessedConfig {
  val elements = mutableListOf<PreferenceElement>()
  val keys = config.keySetIterator()
  var signature = "" // Not fully correct, but should be good enough.
  while (keys.hasNextKey()) {
    val key = keys.nextKey()
    val elData = config.getMap(key)
    val icon = elData?.let { getIcon(it, res) }
    when (elData?.getString("type")) {
      "details" -> {
        val title = elData.getString("title") ?: continue
        val details = elData.getString("details") ?: continue
        elements.add(DetailsElement(key, title, details, icon, elData.getInt("weight")))
        signature += "$key-$title-$details-${icon?.signature}:"
      }
      "list" -> {
        val title = elData.getString("title") ?: continue
        val labels = elData.getArray("labels")?.toStringList() ?: continue
        val values = elData.getArray("values")?.toStringList() ?: continue
        dataStore.putString(key, elData.getString("value"))
        elements.add(ListElement(key, title, labels, values, icon, elData.getInt("weight")))
        signature += "$key-$title-$labels-$values-${icon?.signature}:"
      }
      "switch" -> {
        val title = elData.getString("title") ?: continue
        dataStore.putBoolean(key, elData.getBoolean("value"))
        elements.add(SwitchElement(key, title, icon, elData.getInt("weight")))
        signature += "$key-$title-${icon?.signature}:"
      }
    }
  }
  elements.sortBy { it.weight }
  return ProcessedConfig(elements, signature)
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

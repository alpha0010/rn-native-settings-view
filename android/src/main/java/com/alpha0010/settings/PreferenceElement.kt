package com.alpha0010.settings

import androidx.preference.PreferenceDataStore
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType

sealed class PreferenceElement {
  abstract val key: String
  abstract val weight: Int
}

data class DetailsElement(
  override val key: String,
  val title: String,
  val details: String,
  override val weight: Int
) : PreferenceElement()

data class ListElement(
  override val key: String,
  val title: String,
  val labels: List<String>,
  val values: List<String>,
  override val weight: Int
) : PreferenceElement()

data class SwitchElement(
  override val key: String,
  val title: String,
  override val weight: Int
) : PreferenceElement()

data class ProcessedConfig(
  val elements: List<PreferenceElement>,
  val signature: String
)

fun processSettingsConfig(config: ReadableMap, dataStore: PreferenceDataStore): ProcessedConfig {
  val elements = mutableListOf<PreferenceElement>()
  val keys = config.keySetIterator()
  var signature = "" // Not fully correct, but should be good enough.
  while (keys.hasNextKey()) {
    val key = keys.nextKey()
    val elData = config.getMap(key)
    when (elData?.getString("type")) {
      "details" -> {
        val title = elData.getString("title") ?: continue
        val details = elData.getString("details") ?: continue
        elements.add(DetailsElement(key, title, details, elData.getInt("weight")))
        signature += "$key-$title-$details-"
      }
      "list" -> {
        val title = elData.getString("title") ?: continue
        val labels = elData.getArray("labels")?.toStringList() ?: continue
        val values = elData.getArray("values")?.toStringList() ?: continue
        dataStore.putString(key, elData.getString("value"))
        elements.add(ListElement(key, title, labels, values, elData.getInt("weight")))
        signature += "$key-$title-$labels-$values-"
      }
      "switch" -> {
        val title = elData.getString("title") ?: continue
        dataStore.putBoolean(key, elData.getBoolean("value"))
        elements.add(SwitchElement(key, title, elData.getInt("weight")))
        signature += "$key-$title-"
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

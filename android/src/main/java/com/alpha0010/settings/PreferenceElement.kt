package com.alpha0010.settings

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

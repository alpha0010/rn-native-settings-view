package com.alpha0010.settings

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.facebook.react.views.text.ReactFontManager
import kotlin.math.max

class IconFontDrawable(
  charCode: Int,
  font: String,
  @ColorInt val fg: Int,
  @ColorInt val bg: Int,
  res: Resources
) : Drawable() {
  val signature = "$charCode-$fg-$bg"
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val text = String(intArrayOf(charCode), 0, 1)
  private val tWidth: Float
  private val tHeight: Float

  init {
    paint.style = Paint.Style.FILL
    paint.typeface = ReactFontManager.getInstance().getTypeface(font, 0, res.assets)

    // Compute symbol size.
    paint.textSize = 100f
    val maxDim = max(
      paint.measureText(text, 0, text.length),
      paint.getFontMetricsInt(null).toFloat()
    )
    paint.textSize = 8100f / maxDim

    tWidth = paint.measureText(text, 0, text.length)
    tHeight = paint.getFontMetricsInt(null).toFloat()
  }

  override fun draw(canvas: Canvas) {
    paint.color = bg
    canvas.drawCircle(47f, 47f, 47f, paint)

    paint.color = fg
    canvas.drawText(
      text,
      0,
      text.length,
      (94 - tWidth) / 2,
      (94 + tWidth) / 2,
      paint
    )
  }

  override fun getIntrinsicWidth() = 94

  override fun getIntrinsicHeight() = 94

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  @Deprecated("Deprecated in Java")
  override fun getOpacity() = paint.alpha
}

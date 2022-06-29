package com.alpha0010.settings

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.facebook.react.views.text.ReactFontManager

class IconFontDrawable(charCode: Int, font: String, res: Resources) : Drawable() {
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val text = String(intArrayOf(charCode), 0, 1)
  private val iWidth: Int
  private val iHeight: Int

  init {
    paint.typeface = ReactFontManager.getInstance().getTypeface(font, 0, res.assets)
    paint.color = Color.GRAY
    paint.textSize = 85f
    iWidth = (paint.measureText(text, 0, text.length) + .5).toInt()
    iHeight = paint.getFontMetricsInt(null)
  }

  override fun draw(canvas: Canvas) {
    canvas.drawText(
      text,
      0,
      text.length,
      0f,
      bounds.bottom.toFloat(),
      paint
    )
  }

  override fun getIntrinsicWidth() = iWidth

  override fun getIntrinsicHeight() = iHeight

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  @Deprecated("Deprecated in Java")
  override fun getOpacity() = paint.alpha
}

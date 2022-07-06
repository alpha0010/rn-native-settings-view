package com.alpha0010.settings

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.facebook.react.views.text.ReactFontManager
import kotlin.math.ceil
import kotlin.math.max

class IconFontDrawable(
  charCode: Int,
  font: String,
  @ColorInt val fg: Int,
  @ColorInt val bg: Int,
  res: Resources
) : Drawable() {
  val signature = "$charCode-$fg-$bg"
  private val dim: Int
  private val iconBounds = Rect()
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val text = String(intArrayOf(charCode), 0, 1)

  init {
    val scale = res.displayMetrics.density
    dim = 2 * ceil(17.9 * scale).toInt()

    paint.style = Paint.Style.FILL
    paint.typeface = ReactFontManager.getInstance().getTypeface(font, 0, res.assets)

    // Compute symbol size.
    paint.textSize = 200f
    paint.getTextBounds(text, 0, text.length, iconBounds)
    val maxDim = max(iconBounds.width(), iconBounds.height())

    paint.textSize = 4420 * scale / maxDim
    paint.getTextBounds(text, 0, text.length, iconBounds)
  }

  override fun draw(canvas: Canvas) {
    val dHalf = dim / 2f
    paint.color = bg
    canvas.drawCircle(dHalf, dHalf, dHalf, paint)

    paint.color = fg
    canvas.drawText(
      text,
      0,
      text.length,
      dHalf - iconBounds.centerX(),
      dHalf - iconBounds.centerY(),
      paint
    )
  }

  override fun getIntrinsicWidth() = dim
  override fun getIntrinsicHeight() = dim
  override fun setAlpha(alpha: Int) = Unit

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  @Deprecated("Deprecated in Java")
  override fun getOpacity() = PixelFormat.TRANSLUCENT
}

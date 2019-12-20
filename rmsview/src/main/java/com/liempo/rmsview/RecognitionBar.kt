package com.liempo.rmsview

import android.graphics.RectF

class RecognitionBar(var x: Int, var y: Int,
                     var height: Int,
                     val maxHeight: Int,
                     val radius: Int) {
    val startX = x
    val startY = y
    val rect: RectF = RectF((x - radius).toFloat(),
            (y - height / 2).toFloat(),
            (x + radius).toFloat(),
            (y + height / 2).toFloat())

    fun update() {
        rect.set((x - radius).toFloat(),
                (y - height / 2).toFloat(),
                (x + radius).toFloat(),
                (y + height / 2).toFloat())
    }
}
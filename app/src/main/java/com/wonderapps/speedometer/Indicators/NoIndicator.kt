package com.wonderapps.speedometer.Indicators

import android.content.Context
import android.graphics.Canvas


class NoIndicator(context: Context) : Indicator<NoIndicator>(context) {

    override val defaultIndicatorWidth: Float
        get() = 0f

    override fun draw(canvas: Canvas, degree: Float) {}

    override fun updateIndicator() {}

    override fun setWithEffects(withEffects: Boolean) {}
}

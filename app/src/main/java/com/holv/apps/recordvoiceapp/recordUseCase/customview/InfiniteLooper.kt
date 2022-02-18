package com.holv.apps.recordvoiceapp.recordUseCase.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.holv.apps.recordvoiceapp.R

class InfiniteLooper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pathLineUpDown = Path()
    private val pathCircle = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 63f
        color = ContextCompat.getColor(context, R.color.orange)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val xCenter = (width / WIDTH_DIVIDER)
        val yCenter = (height / HEIGHT_DIVIDER)

        pathLineUpDown.reset()
        pathLineUpDown.moveTo(xCenter - HOUNDRED_AND_FIFTY, yCenter + SIXTY)
        pathLineUpDown.lineTo(xCenter + HOUNDRED_AND_FIFTY,yCenter + SIXTY)

        pathCircle.reset()
        pathCircle.moveTo(xCenter, yCenter)
        pathCircle.addCircle(xCenter, yCenter, 20f, Path.Direction.CW)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(pathLineUpDown, paint)
        canvas?.drawPath(pathCircle, paint)
    }

    companion object {
        private const val WIDTH_DIVIDER = 2.0f
        private const val HEIGHT_DIVIDER = 4.0f
        private const val HOUNDRED_AND_FIFTY = 150f
        private const val SIXTY = 60f
    }
}
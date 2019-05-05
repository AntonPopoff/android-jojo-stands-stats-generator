package com.antonpopoff.standcharacteristicsgenerator.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.antonpopoff.standcharacteristicsview.utils.toRadians
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ColorWheel @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        ****************************************

//
//        val s = 1f
//        var angle = 0f
//
//        while (angle < 360f) {
//            canvas.drawArc(
//                    0f,
//                    0f,
//                    width.toFloat(),
//                    height.toFloat(),
//                    angle,
//                    s,
//                    true,
//                    paint
//            )
//
//            angle += s - 0.5f
//        }
//
//        canvas.drawPoint()

//        ****************************************

//        val cx = width / 2f
//        val cy = height / 2f
//        val r = min(width, height) / 2
//
////        for (i in 0 until 360) {
////            val x = r * cos(toRadians(i.toFloat())) + cx
////            val y = r * sin(toRadians(i.toFloat())) + cy
////            canvas.drawLine(cx, cy, x, y, paint)
////        }
//
//        var angle = 0f
//        val step = 0.1f
//
//        while (angle < 360) {
//            val x = r * cos(toRadians(angle)) + cx
//            val y = r * sin(toRadians(angle)) + cy
//            canvas.drawLine(cx, cy, x, y, paint)
//
//            angle += step
//        }

//        ****************************************

        val r = min(width, height) / 2
        val cx = width / 2f
        val cy = height / 2f

        val hsv = floatArrayOf(0f, 1f, 1f)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val k0 = abs((cy - j).toDouble())
                val k1 = abs((cx - i).toDouble())
                val ratio = k0 / k1

                var angle = Math.toDegrees(Math.atan(ratio)).toFloat() + 90

                hsv[0] = angle
                hsv[1] = Math.sqrt(k0 * k0 + k1 * k1).toFloat() / r

                paint.color = Color.HSVToColor(hsv)

                canvas.drawPoint(i.toFloat(),j.toFloat(),paint)
            }
        }
    }
}
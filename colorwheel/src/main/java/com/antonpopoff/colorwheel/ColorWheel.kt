package com.antonpopoff.colorwheel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.antonpopoff.colorwheel.utils.*
import kotlin.math.*

class ColorWheel(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val viewConfig = ViewConfiguration.get(context)

    private var gradientsInitialized = false
    private val hueGradient = createOvalGradient(GradientDrawable.SWEEP_GRADIENT)
    private val saturationGradient = createOvalGradient(GradientDrawable.RADIAL_GRADIENT)

    private val wheelCenter = PointF()
    private var wheelRadius = 0f

    private val thumbDrawable = ThumbDrawable()
    private val hsvColor = HsvColor(value = 1f)

    private var motionEventDownX = 0f

    var colorChangeListener: ((Int) -> Unit)? = null

    val argb get() = hsvColor.toArgb()

    var thumbRadius = 0f
        set(value) {
            thumbDrawable.applyInsets(value)
            field = value
            invalidate()
        }

    init {
        parseAttributes(context, attrs)
        thumbDrawable.applyInsets(thumbRadius)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.style.ColorWheelDefaultStyle)

    constructor(context: Context) : this(context, null)

    private fun parseAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ColorWheel, 0, R.style.ColorWheelDefaultStyle).apply {
            thumbRadius = getDimension(R.styleable.ColorWheel_cw_thumbRadius, 0f)
            recycle()
        }
    }

    private fun createOvalGradient(type: Int) = GradientDrawable().apply {
        gradientType = type
        shape = GradientDrawable.OVAL
    }

    fun setColor(argb: Int) {
        hsvColor.set(argb)
        fireColorListener()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        ensureGradientsInitialized()
        calculateWheelProperties()
        calculateThumbRect()
        calculateGradientBounds()
        hueGradient.draw(canvas)
        saturationGradient.draw(canvas)
        drawThumb(canvas)
    }

    private fun ensureGradientsInitialized() {
        if (!gradientsInitialized) {
            hueGradient.colors = calculateHueColors()
            saturationGradient.colors = intArrayOf(Color.WHITE, Color.TRANSPARENT)
            gradientsInitialized = true
        }
    }

    private fun calculateHueColors(): IntArray {
        val step = 1f
        val size = ceil((360 / step)).toInt()
        var angle = 0f
        var index = 0
        val hueColors = IntArray(size)
        val tmpHsvColor = HsvColor(saturation = 1f, value = 1f)

        while (angle < 360f) {
            tmpHsvColor.hue = angle
            hueColors[index++] = tmpHsvColor.toArgb()
            angle += step
        }

        return hueColors
    }

    private fun calculateGradientBounds() {
        val left = (wheelCenter.x - wheelRadius).toInt()
        val top = (wheelCenter.y - wheelRadius).toInt()
        val right = (wheelCenter.x + wheelRadius).toInt()
        val bottom = (wheelCenter.y + wheelRadius).toInt()

        hueGradient.setBounds(left, top, right, bottom)

        saturationGradient.apply {
            setBounds(left, top, right, bottom)
            gradientRadius = wheelRadius
        }
    }

    private fun calculateWheelProperties() {
        val hSpace = (width - paddingLeft - paddingRight) / 2f
        val vSpace = (height - paddingTop - paddingBottom) / 2f

        wheelRadius = min(hSpace, vSpace)

        wheelCenter.apply {
            x = paddingLeft + hSpace
            y = paddingTop + vSpace
        }
    }

    private fun calculateThumbRect() {
        val r = hsvColor.saturation * wheelRadius
        val hueRadians = toRadians(hsvColor.hue)

        val thumbX = cos(hueRadians) * r + wheelCenter.x
        val thumbY = sin(hueRadians) * r + wheelCenter.y

        val left = (thumbX - thumbRadius).toInt()
        val top = (thumbY - thumbRadius).toInt()
        val right = (thumbX + thumbRadius).toInt()
        val bottom = (thumbY + thumbRadius).toInt()

        thumbDrawable.setBounds(left, top, right, bottom)
    }

    private fun drawThumb(canvas: Canvas) {
        thumbDrawable.apply {
            indicatorColor = hsvColor.toArgb()
            draw(canvas)
        }
    }

    private fun fireColorListener() {
        colorChangeListener?.invoke(hsvColor.toArgb())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                motionEventDownX = event.x
                updateColorOnMotionEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateColorOnMotionEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                updateColorOnMotionEvent(event)
                if (isTap(event)) performClick()
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick() = super.performClick()

    private fun updateColorOnMotionEvent(event: MotionEvent) {
        val legX = event.x - wheelCenter.x
        val legY = event.y - wheelCenter.y
        val r = calculateRadius(legX, legY)
        val angle = atan2(legY, legX)
        val x = cos(angle) * r + wheelCenter.x
        val y = sin(angle) * r + wheelCenter.y

        calculateColor(x, y, wheelCenter.x, wheelCenter.y, wheelRadius)
        fireColorListener()
        invalidate()
    }

    private fun calculateRadius(legX: Float, legY: Float): Float {
        val radius = sqrt(legX * legX + legY * legY)
        return if (radius > wheelRadius) wheelRadius else radius
    }

    private fun calculateColor(x: Float, y: Float, cx: Float, cy: Float, r: Float) {
        val dx = x - cx
        val dy = y - cy

        val hue = (toDegrees(atan2(dy, dx)) + 360) % 360
        val saturation = sqrt(dx * dx + dy * dy) / wheelRadius

        hsvColor.set(hue, saturation, 1f)
    }

    private fun isTap(event: MotionEvent): Boolean {
        val eventDuration = event.eventTime - event.downTime
        val eventTravelDistance = abs(event.x - motionEventDownX)
        return eventDuration < ViewConfiguration.getTapTimeout() && eventTravelDistance < viewConfig.scaledTouchSlop
    }
}

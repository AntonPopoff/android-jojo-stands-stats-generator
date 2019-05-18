package com.antonpopoff.colorwheel

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.antonpopoff.colorwheel.utils.clearAlpha
import com.antonpopoff.colorwheel.utils.setAlpha
import kotlin.math.abs
import kotlin.math.roundToInt

private const val MAX_ALPHA = 255

class AlphaSeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val viewConfig = ViewConfiguration.get(context)

    private val gradientRect = Rect()
    private val gradientColors = IntArray(2)
    private val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors)

    private var motionEventDownX = 0f
    private var thumbRadius = 0
    private val thumbRect = Rect()
    private val thumbDrawable = ThumbDrawable()
    private var barWidth = 0

    val originColor get() = gradientColors[1]

    val argb get() = setAlpha(originColor, colorAlpha)

    var colorAlpha = MAX_ALPHA
        private set

    var alphaChangeListener: ((Int) -> Unit)? = null

    init {
        parseAttributes(context, attrs, R.style.AlphaSeekBarDefaultStyle)
        thumbDrawable.applyInsets(thumbRadius.toFloat())
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private fun parseAttributes(context: Context, attrs: AttributeSet?, defStyle: Int) {
        context.obtainStyledAttributes(attrs, R.styleable.AlphaSeekBar, 0, defStyle).apply {
            thumbRadius = getDimensionPixelOffset(R.styleable.AlphaSeekBar_asb_thumbRadius, 0)
            barWidth = getDimensionPixelOffset(R.styleable.AlphaSeekBar_asb_barWidth, 0)
            colorAlpha = readAlpha(this)
            setOriginColor(getColor(R.styleable.AlphaSeekBar_asb_color, Color.BLACK))
            recycle()
        }
    }

    private fun readAlpha(typedArray: TypedArray): Int {
        val alphaFraction = typedArray.getFloat(R.styleable.AlphaSeekBar_asb_alpha, 1f)
        val checkedAlpha = when {
            alphaFraction < 0 -> 0f
            alphaFraction > 1 -> 1f
            else -> alphaFraction
        }

        return (checkedAlpha * 255).roundToInt()
    }

    fun setAlpha(alpha: Int) {
        colorAlpha = when {
            alpha < 0 -> 0
            alpha > MAX_ALPHA -> MAX_ALPHA
            else -> alpha
        }

        fireListener()
        invalidate()
    }

    fun setOriginColor(argb: Int) {
        gradientColors[0] = clearAlpha(argb)
        gradientColors[1] = setAlpha(argb, MAX_ALPHA)
        gradient.colors = gradientColors
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredWidth = maxOf(barWidth, thumbRadius * 2)
        val preferredHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        calculateGradientRect()
        calculateThumbRect()
        updateIndicatorColor()
        drawGradientRect(canvas)
        drawThumb(canvas)
    }

    private fun calculateGradientRect() {
        gradientRect.apply {
            left = paddingLeft + (width - paddingLeft - paddingRight - barWidth) / 2
            right = left + barWidth
            top = paddingTop + thumbRadius
            bottom = height - paddingBottom - thumbRadius
        }
    }

    private fun drawGradientRect(canvas: Canvas) {
        gradient.apply {
            bounds = gradientRect
            cornerRadius = gradientRect.width() / 2f
            draw(canvas)
        }
    }

    private fun calculateThumbRect() {
        val thumbDiameter = thumbRadius * 2
        val thumbY = convertAlphaToThumbPosition(colorAlpha)

        thumbRect.apply {
            left = gradientRect.centerX() - thumbRadius
            right = left + thumbDiameter
            top = thumbY - thumbRadius
            bottom = top + thumbDiameter
        }
    }

    private fun drawThumb(canvas: Canvas) {
        thumbDrawable.apply {
            bounds = thumbRect
            draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                motionEventDownX = event.x
                calculateAlphaOnMotionEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                calculateAlphaOnMotionEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                calculateAlphaOnMotionEvent(event)
                if (isTap(event)) performClick()
            }
        }

        return super.onTouchEvent(event)
    }

    private fun calculateAlphaOnMotionEvent(event: MotionEvent) {
        colorAlpha = calculateAlphaByMotionEventY(ensureMotionEventYInBounds(event))
        fireListener()
        invalidate()
    }

    private fun ensureMotionEventYInBounds(event: MotionEvent) = when {
        event.y > gradientRect.bottom -> gradientRect.bottom
        event.y < gradientRect.top -> gradientRect.top
        else -> event.y.roundToInt()
    }

    private fun calculateAlphaByMotionEventY(y: Int): Int {
        val relativeThumbY = (y - gradientRect.top).toFloat()
        return MAX_ALPHA - ((relativeThumbY / gradientRect.height()) * MAX_ALPHA).roundToInt()
    }

    private fun updateIndicatorColor() {
        thumbDrawable.indicatorColor = setAlpha(originColor, colorAlpha)
    }

    private fun isTap(event: MotionEvent): Boolean {
        val eventDuration = event.eventTime - event.downTime
        val eventTravelDistance = abs(event.x - motionEventDownX)
        return eventDuration < ViewConfiguration.getTapTimeout() && eventTravelDistance < viewConfig.scaledTouchSlop
    }

    private fun convertAlphaToThumbPosition(alpha: Int): Int {
        val alphaNormalized = 1 - (alpha.toFloat() / MAX_ALPHA)
        return (gradientRect.top + alphaNormalized * gradientRect.height()).roundToInt()
    }

    private fun fireListener() {
        alphaChangeListener?.invoke(colorAlpha)
    }

    override fun performClick() = super.performClick()
}

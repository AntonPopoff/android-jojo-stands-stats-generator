package com.antonpopoff.colorwheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class AlphaSeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val gradientRect = Rect()
    private val gradientColors = IntArray(2)
    private val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors)

    private var thumbY = 0
    private var thumbRadius = 0
    private val thumbRect = Rect()
    private val thumbDrawable = ThumbDrawable()
    private var barWidth = 0

    var color
        get() = gradientColors[1]
        set(value) {
            gradientColors[1] = value
            invalidate()
        }

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
            recycle()
        }
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
        ensureThumbYInitialized()
        calculateThumbRect()
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

        thumbRect.apply {
            left = gradientRect.centerX() - thumbRadius
            right = left + thumbDiameter
            top = thumbY - thumbRadius
            bottom = top + thumbDiameter
        }
    }

    private fun ensureThumbYInitialized() {
        if (thumbY == 0) {
            thumbY = gradientRect.top
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
                updateThumbY(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateThumbY(event)
            }
        }

        return super.onTouchEvent(event)
    }

    private fun updateThumbY(event: MotionEvent) {
        thumbY = when {
            event.y > gradientRect.bottom -> gradientRect.bottom
            event.y < gradientRect.top -> gradientRect.top
            else -> event.y.roundToInt()
        }

        invalidate()
    }

    override fun performClick() = super.performClick()
}
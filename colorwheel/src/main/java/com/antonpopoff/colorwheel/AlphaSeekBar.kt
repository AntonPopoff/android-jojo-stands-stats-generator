package com.antonpopoff.colorwheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View

class AlphaSeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val gradientRect = Rect()
    private val gradientColors = IntArray(2)
    private val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors)

    private val thumbDrawable = ThumbDrawable()

    private var thumbRadius = 0
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
        val preferredWidth = maxOf(barWidth, thumbRadius)
        val preferredHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        calculateGradientRect()
        drawGradientRect(canvas)
    }

    private fun calculateGradientRect() {
        gradientRect.apply {
            left = paddingLeft + (width - paddingLeft - paddingRight - barWidth) / 2
            right = left + barWidth
            top = paddingTop
            bottom = height - paddingBottom
        }
    }

    private fun drawGradientRect(canvas: Canvas) {
        gradient.apply {
            bounds = gradientRect
            cornerRadius = gradientRect.width() / 2f
            draw(canvas)
        }
    }
}

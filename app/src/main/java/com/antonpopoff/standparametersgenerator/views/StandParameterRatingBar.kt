package com.antonpopoff.standparametersgenerator.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.extensions.getTextHeight
import com.antonpopoff.standparametersview.diagram.ParameterRating
import kotlin.math.abs
import kotlin.math.roundToInt

class StandParameterRatingBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val thumbXAnimator = ValueAnimator().apply {
        addUpdateListener(ThumbXAnimatorUpdateListener())
        duration = 200
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val viewConfiguration = ViewConfiguration.get(context)
    private val defaultTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    private val totalRatingRect = RectF()
    private val totalRatingRectCopy = RectF()
    private val actualRatingRect = RectF()
    private val textRect = Rect()

    private var selectedBarColor = 0
    private var barColor = 0
    private var barHeight = 0f
    private var thumbRadius = 0f
    private var notchesRadius = 0f
    private var textOffset = 0f
    private val textSize get() = textPaint.textSize

    private var thumbDestination = 0f
    private var distanceBetweenNotches = 0f
    private var ratingBarOccupiedHeight = 0f
    private var sidesOffset = 0f
    private var activeTouch = false
    private var thumbX = 0f
    private var downX = 0f

    var rating = ParameterRating.ratings.first()
        private set

    init {
        attrs?.let { parseAttributes(context, it) }
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.StandParameterRatingBar, 0, R.style.ParametersRatingBarDefaultStyle).apply {
            barHeight = getDimension(R.styleable.StandParameterRatingBar_rb_barHeight, 0f)
            thumbRadius = getDimension(R.styleable.StandParameterRatingBar_rb_thumbRadius, 0f)
            notchesRadius = getDimension(R.styleable.StandParameterRatingBar_rb_notchRadius, 0f)
            textPaint.textSize = getDimension(R.styleable.StandParameterRatingBar_rb_textSize, 0f)
            textOffset = getDimension(R.styleable.StandParameterRatingBar_rb_textOffset, 0f)
            barColor = getColor(R.styleable.StandParameterRatingBar_rb_barColor, 0)
            selectedBarColor = getColor(R.styleable.StandParameterRatingBar_rb_selectedBarColor, 0)
            recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    fun setRating(rating: ParameterRating, animate: Boolean = true) {
        this.rating = rating
        updateThumbPositionOnRatingChange(animate)
    }

    private fun updateThumbPositionOnRatingChange(animate: Boolean) {
        thumbDestination = getThumbDestination(rating.ordinal)

        if (animate) {
            startThumbAnimation(thumbDestination)
        } else {
            thumbX = thumbDestination
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        calcRatingBarOccupiedHeight()

        val preferredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val preferredHeight = (ratingBarOccupiedHeight + textOffset + textSize + paddingTop + paddingBottom).roundToInt()

        setMeasuredDimension(
                resolveSize(preferredWidth, widthMeasureSpec),
                resolveSize(preferredHeight, heightMeasureSpec)
        )
    }

    private fun calcRatingBarOccupiedHeight() {
        ratingBarOccupiedHeight = maxOf(thumbRadius * 2, notchesRadius * 2, barHeight)
    }

    override fun onDraw(canvas: Canvas) {
        calcSidesOffset()
        calcTotalRatingBarRect()
        adjustThumbPositionIfBoundsChanged()
        calcDistanceBetweenNotches()
        ensureThumbIsInCorrectPosition()
        calcActualRatingBarRect()

        drawRect(canvas, totalRatingRect, barColor)
        drawRect(canvas, actualRatingRect, selectedBarColor)
        drawNotches(canvas)
        drawThumb(canvas)
        drawRatingText(canvas)
    }

    private fun calcSidesOffset() {
        textPaint.typeface = boldTypeface

        val firstRatingCharWidth = textPaint.measureText(ParameterRating.ratings.first().char) / 2
        val lastRatingCharWidth = textPaint.measureText(ParameterRating.ratings.last().char) / 2
        val maxCharWidth = maxOf(firstRatingCharWidth, lastRatingCharWidth)

        sidesOffset = maxOf(thumbRadius, notchesRadius, maxCharWidth)
    }

    private fun calcTotalRatingBarRect() {
        val availableHeight = height - paddingTop - paddingBottom - textSize - textOffset

        totalRatingRect.apply {
            left = paddingLeft + sidesOffset
            right = width - paddingRight - sidesOffset
            top = paddingTop + (availableHeight - barHeight) / 2
            bottom = top + barHeight
        }
    }

    private fun adjustThumbPositionIfBoundsChanged() {
        if (totalRatingRectCopy.isEmpty) {
            totalRatingRectCopy.set(totalRatingRect)
        } else if (totalRatingRect != totalRatingRectCopy) {
            val relativePos = (thumbX - totalRatingRectCopy.left) / totalRatingRectCopy.width()
            thumbX = relativePos * totalRatingRect.width() + totalRatingRect.left
            totalRatingRectCopy.set(totalRatingRect)
        }
    }

    private fun calcActualRatingBarRect() {
        actualRatingRect.apply {
            set(totalRatingRect)
            right = thumbX
        }
    }

    private fun calcDistanceBetweenNotches() {
        distanceBetweenNotches = totalRatingRect.width() / (ParameterRating.ratingsCount - 1)
    }

    private fun ensureThumbIsInCorrectPosition() {
        if (!activeTouch && !thumbXAnimator.isRunning) {
            thumbX = getThumbDestination(rating.ordinal)
        } else if (thumbXAnimator.isRunning) {
            adjustThumbAnimationIfNeed()
        }
    }

    private fun adjustThumbAnimationIfNeed() {
        val actualThumbDestination = getThumbDestination(rating.ordinal)

        if (thumbDestination != actualThumbDestination) {
            thumbDestination = actualThumbDestination

            thumbXAnimator.apply {
                cancel()
                setFloatValues(thumbX, actualThumbDestination)
                start()
            }
        }
    }

    private fun drawRect(canvas: Canvas, rectF: RectF, color: Int) {
        paint.color = color
        canvas.drawRect(rectF, paint)
    }

    private fun drawThumb(canvas: Canvas) {
        paint.color = selectedBarColor
        canvas.drawCircle(thumbX, totalRatingRect.centerY(), thumbRadius, paint)
    }

    private fun drawNotches(canvas: Canvas) {
        for (i in 0 until ParameterRating.ratingsCount) {
            val notchX = totalRatingRect.left + i * distanceBetweenNotches
            paint.color = getNotchColor(notchX)
            canvas.drawCircle(notchX, totalRatingRect.centerY(), notchesRadius, paint)
        }
    }

    private fun getNotchColor(notchX: Float) = if (notchX > thumbX) {
        barColor
    } else {
        selectedBarColor
    }

    private fun drawRatingText(canvas: Canvas) {
        val textTop = paddingTop + ratingBarOccupiedHeight + textOffset

        for (i in 0 until ParameterRating.ratingsCount) {
            textPaint.apply {
                color = getTextColor(i)
                typeface = getTextTypeface(i)
            }

            val char = ParameterRating.ratings[i].char
            val charWidth = textPaint.measureText(char)
            val charHeight = textPaint.getTextHeight(char, textRect)
            val charX = totalRatingRect.left + distanceBetweenNotches * i - charWidth / 2
            val charY = textTop + (textSize + charHeight) / 2

            canvas.drawText(char, charX, charY, textPaint)
        }
    }

    private fun getTextColor(ratingIndex: Int) = if (ratingIndex == getDestinationRatingIndex()) {
        selectedBarColor
    } else {
        barColor
    }

    private fun getTextTypeface(ratingIndex: Int) = if (ratingIndex == getDestinationRatingIndex()) {
        boldTypeface
    } else {
        defaultTypeface
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        activeTouch = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                thumbXAnimator.cancel()
                updateThumbPositionOnMotionEvent(event.x)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                updateThumbPositionOnMotionEvent(event.x)
            }
            MotionEvent.ACTION_UP -> {
                updateThumbPositionOnMotionEvent(event.x)
                animateThumbToFinalPosition()
                if (isTap(event)) performClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                updateThumbPositionOnMotionEvent(event.x)
                animateThumbToFinalPosition()
            }
        }

        return false
    }

    override fun performClick() = super.performClick()

    private fun isTap(event: MotionEvent): Boolean {
        val eventDuration = event.eventTime - event.downTime
        val eventTravelDistance = abs(event.x - downX)
        return eventDuration < ViewConfiguration.getTapTimeout() && eventTravelDistance < viewConfiguration.scaledTouchSlop
    }

    private fun animateThumbToFinalPosition() {
        val ratingIndex = getDestinationRatingIndex()
        thumbDestination = getThumbDestination(ratingIndex)
        rating = ParameterRating.ratings[ratingIndex]
        startThumbAnimation(thumbDestination)
    }

    private fun startThumbAnimation(destination: Float) {
        thumbXAnimator.apply {
            setFloatValues(thumbX, destination)
            start()
        }
    }

    private fun getDestinationRatingIndex() = ((thumbX - sidesOffset - paddingLeft) / distanceBetweenNotches).roundToInt()

    private fun getThumbDestination(ratingIndex: Int) = ratingIndex * distanceBetweenNotches + totalRatingRect.left

    private fun updateThumbPositionOnMotionEvent(x: Float) {
        thumbX = when {
            x < totalRatingRect.left -> totalRatingRect.left
            x > totalRatingRect.right -> totalRatingRect.right
            else -> x
        }

        invalidate()
    }

    private inner class ThumbXAnimatorUpdateListener : ValueAnimator.AnimatorUpdateListener {

        override fun onAnimationUpdate(animation: ValueAnimator) {
            thumbX = animation.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }
}

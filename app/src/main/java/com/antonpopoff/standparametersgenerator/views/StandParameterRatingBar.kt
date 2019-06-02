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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val viewConfiguration = ViewConfiguration.get(context)
    private val totalRatingRect = RectF()
    private val actualRatingRect = RectF()
    private val textRect = Rect()

    private var selectedBarColor = 0
    private var barColor = 0
    private var barHeight = 0f
    private var thumbRadius = 0f
    private var notchesRadius = 0f
    private var textOffset = 0f
    private val textSize get() = textPaint.textSize

    private var ratingBarOccupiedHeight = 0f
    private var sidesOffset = 0f
    private var downX = 0f
    private var offsetThumbX = 0f
    private var internalRating = 0f

    private val internalRatingAnimator = ValueAnimator().also {
        it.addUpdateListener(this::onInternalRatingAnimatorUpdate)
        it.duration = 200
    }

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

    private fun onInternalRatingAnimatorUpdate(animator: ValueAnimator) {
        internalRating = animator.animatedValue as Float
        postInvalidateOnAnimation()
    }

    fun setRating(rating: ParameterRating, animate: Boolean = true) {
        this.rating = rating
        updateThumbPositionOnRatingChange(animate)
    }

    private fun updateThumbPositionOnRatingChange(animate: Boolean) {
        val distance = 1f / (ParameterRating.ratingsCount - 1)
        val destination = distance * rating.ordinal

        if (animate) {
            startInternalRatingAnimator(destination)
        } else {
            internalRating = destination
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
        calcOffsetThumbX()
        calcActualRatingBarRect()

        drawRect(canvas, totalRatingRect, barColor)
        drawRect(canvas, actualRatingRect, selectedBarColor)
        drawNotches(canvas)
        drawThumb(canvas)
        drawRatingText(canvas)
    }

    private fun calcSidesOffset() {
        textPaint.typeface = Typeface.DEFAULT_BOLD

        val firstRatingCharWidth = textPaint.measureText(ParameterRating.ratings.first().char) / 2
        val lastRatingCharWidth = textPaint.measureText(ParameterRating.ratings.last().char) / 2
        val maxCharWidth = maxOf(firstRatingCharWidth, lastRatingCharWidth)

        sidesOffset = maxOf(thumbRadius, notchesRadius, maxCharWidth)
    }

    private fun calcTotalRatingBarRect() {
        val vSpace = height - paddingTop - paddingBottom - textSize - textOffset
        val left = paddingLeft + sidesOffset
        val right = width - paddingRight - sidesOffset
        val top = paddingTop + (vSpace - barHeight) / 2
        val bottom = top + barHeight

        totalRatingRect.set(left, top, right, bottom)
    }

    private fun calcOffsetThumbX() {
        offsetThumbX = totalRatingRect.left + internalRating * totalRatingRect.width()
    }

    private fun calcActualRatingBarRect() {
        actualRatingRect.apply {
            set(totalRatingRect)
            right = offsetThumbX
        }
    }

    private fun drawRect(canvas: Canvas, rectF: RectF, color: Int) {
        paint.color = color
        canvas.drawRect(rectF, paint)
    }

    private fun drawThumb(canvas: Canvas) {
        paint.color = selectedBarColor
        canvas.drawCircle(offsetThumbX, totalRatingRect.centerY(), thumbRadius, paint)
    }

    private fun drawNotches(canvas: Canvas) {
        val distanceBetweenNotches = totalRatingRect.width() / (ParameterRating.ratingsCount - 1)

        for (i in 0 until ParameterRating.ratingsCount) {
            val x = totalRatingRect.left + i * distanceBetweenNotches
            paint.color = if (x > offsetThumbX) barColor else selectedBarColor
            canvas.drawCircle(x, totalRatingRect.centerY(), notchesRadius, paint)
        }
    }

    private fun drawRatingText(canvas: Canvas) {
        val textTop = paddingTop + ratingBarOccupiedHeight + textOffset
        val distanceBetweenLetters = totalRatingRect.width() / (ParameterRating.ratingsCount - 1)
        val selectedLetterIndex = ((offsetThumbX - totalRatingRect.left) / distanceBetweenLetters).roundToInt()

        for (i in 0 until ParameterRating.ratingsCount) {
            val char = ParameterRating.ratings[i].char
            val charWidth = textPaint.measureText(char)
            val charHeight = textPaint.getTextHeight(char, textRect)
            val charX = totalRatingRect.left + distanceBetweenLetters * i - charWidth / 2
            val charY = textTop + (textSize + charHeight) / 2

            setupTextPaint(i, selectedLetterIndex)
            canvas.drawText(char, charX, charY, textPaint)
        }
    }

    private fun setupTextPaint(currentLetterIndex: Int, selectedLetterIndex: Int) {
        textPaint.apply {
            if (currentLetterIndex == selectedLetterIndex) {
                color = selectedBarColor
                typeface = Typeface.DEFAULT_BOLD
            } else {
                color = barColor
                typeface = Typeface.DEFAULT
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                internalRatingAnimator.cancel()
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
        val distanceBetweenRatings = 1f / (ParameterRating.ratingsCount - 1)
        val ratingIndex = (internalRating / distanceBetweenRatings).roundToInt()
        val internalRatingDestination = ratingIndex * distanceBetweenRatings
        rating = ParameterRating.ratings[ratingIndex]
        startInternalRatingAnimator(internalRatingDestination)
    }

    private fun startInternalRatingAnimator(destinationRating: Float) {
        internalRatingAnimator.apply {
            cancel()
            setFloatValues(internalRating, destinationRating)
            start()
        }
    }

    private fun updateThumbPositionOnMotionEvent(x: Float) {
        internalRating = (when {
            x < totalRatingRect.left -> totalRatingRect.left
            x > totalRatingRect.right -> totalRatingRect.right
            else -> x
        } - totalRatingRect.left) / totalRatingRect.width()

        invalidate()
    }
}

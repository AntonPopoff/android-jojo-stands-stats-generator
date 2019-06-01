package com.antonpopoff.standparametersview.diagram

class DiagramValues {

    /* Predefined diagram values and ratios */

    private val innerBorderRadiusToOuterRatio = 0.9f

    private val parametersCircleRadiusToOuterRatio = 0.575f

    private val outerBorderWidthToOuterRadiusRatio = 0.02f

    private val innerBorderWidthToOuterRadiusRatio = 0.015f

    private val parametersCircleWidthToOuterRadiusRatio = 0.01f

    private val parametersNotchLenToRatingCircleRadiusRatio = 0.075f

    val bigBorderNotchesCount = 2

    val smallBorderNotchesCount = 20

    val bigBorderNotchAngle = 3.5f

    val smallBorderNotchAngle = 2.5f

    /* Calculated values */

    var centerX = 0f
        private set

    var centerY = 0f
        private set

    var outerBorderRadius = 0f
        private set

    var innerBorderRadius = 0f
        private set

    var parametersCircleRadius = 0f
        private set

    var outerBorderWidth = 0f
        private set

    var innerBorderWidth = 0f
        private set

    var parametersLinesWidth = 0f
        private set

    var borderNotchWidth = 0f
        private set

    var borderNotchRadius = 0f
        private set

    var ratingNotchLen = 0f
        private set

    var angleBetweenParameters = 0f
        private set

    var spaceBetweenRatings = 0f
        private set

    var ratingNotchLeft = 0f
        private set

    var ratingNotchRight = 0f
        private set

    var parametersNameTextSize = 0f
        private set

    var parametersNameCircleRadius = 0f
        private set

    var ratingLetterCircleRadius = 0f
        private set

    fun calculate(width: Int, height: Int, paddingLeft: Int, paddingTop: Int, paddingBottom: Int, paddingRight: Int) {
        val availableWidth = (width - paddingLeft - paddingRight)
        val availableHeight = (height - paddingTop - paddingBottom)
        calculateCenter(availableWidth, availableHeight, paddingLeft, paddingTop)
        calculateBorderCirclesValues(availableWidth, availableHeight)
        calculateBorderNotchesValues()
        calculateParametersValues()
        calculateRatingValues()
    }

    private fun calculateCenter(availableWidth: Int, availableHeight: Int, paddingLeft: Int, paddingTop: Int) {
        centerX = availableWidth / 2f + paddingLeft
        centerY = availableHeight / 2f + paddingTop
    }

    private fun calculateBorderCirclesValues(availableWidth: Int, availableHeight: Int) {
        outerBorderRadius = minOf(availableWidth, availableHeight) / 2f
        innerBorderRadius = outerBorderRadius * innerBorderRadiusToOuterRatio
        outerBorderWidth = outerBorderRadius * outerBorderWidthToOuterRadiusRatio
        innerBorderWidth = outerBorderRadius * innerBorderWidthToOuterRadiusRatio
    }

    private fun calculateBorderNotchesValues() {
        borderNotchWidth = outerBorderRadius - innerBorderRadius
        borderNotchRadius = innerBorderRadius + borderNotchWidth / 2
    }

    private fun calculateParametersValues() {
        parametersCircleRadius = outerBorderRadius * parametersCircleRadiusToOuterRatio
        parametersLinesWidth = outerBorderRadius * parametersCircleWidthToOuterRadiusRatio
        parametersNameTextSize = (innerBorderRadius - parametersCircleRadius) / 3
        parametersNameCircleRadius = innerBorderRadius - parametersNameTextSize
        angleBetweenParameters = 360f / ParameterName.count
    }

    private fun calculateRatingValues() {
        spaceBetweenRatings = parametersCircleRadius / (ParameterRating.letterRatings.size + 1)
        ratingNotchLen = parametersCircleRadius * parametersNotchLenToRatingCircleRadiusRatio
        ratingNotchLeft = centerX - ratingNotchLen / 2
        ratingNotchRight = ratingNotchLeft + ratingNotchLen
        ratingLetterCircleRadius = innerBorderRadius - parametersNameTextSize * 2
    }
}

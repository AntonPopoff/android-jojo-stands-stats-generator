package com.antonpopoff.standparametersgenerator.dialogs

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.antonpopoff.standparametersgenerator.R
import com.apandroid.colorwheel.ColorWheel
import com.apandroid.colorwheel.gradientseekbar.GradientSeekBar
import com.apandroid.colorwheel.gradientseekbar.setAlphaArgb
import com.apandroid.colorwheel.gradientseekbar.setAlphaListener
import com.apandroid.colorwheel.gradientseekbar.setAlphaRgb

class EditDiagramColorDialog(
        context: Context,
        private var diagramColor: Int,
        private val listener: Listener
) : BottomSheetDialog(context) {

    private lateinit var colorWheel: ColorWheel
    private lateinit var alphaSeekBar: GradientSeekBar
    private lateinit var selectedColorView: View
    private lateinit var resetButton: View
    private lateinit var applyButton: View
    private lateinit var cancelButton: View

    override fun provideDialogContentView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.dialog_edit_diagram_color, container, false)
    }

    override fun onViewAddedToDialog(view: View) {
        findViews(view)

        resetButton.setOnClickListener { resetColors() }
        applyButton.setOnClickListener { onApplyButtonClick() }
        cancelButton.setOnClickListener { dismiss() }

        setupSelectedColorView()
        setupColorWheel()
        setupAlphaSeekBar()
    }

    private fun findViews(view: View) {
        colorWheel = view.findViewById(R.id.colorWheel)
        alphaSeekBar = view.findViewById(R.id.alphaSeekBar)
        selectedColorView = view.findViewById(R.id.selectedColorView)
        resetButton = view.findViewById(R.id.resetButton)
        applyButton = view.findViewById(R.id.applyButton)
        cancelButton = view.findViewById(R.id.cancelButton)
    }

    private fun resetColors() {
        colorWheel.rgb = diagramColor
        alphaSeekBar.setAlphaArgb(diagramColor)
    }

    private fun setupSelectedColorView() {
        selectedColorView.background = GradientDrawable().apply {
            cornerRadius = context.resources.getDimension(R.dimen.dialog_corners_radius)
            setColor(diagramColor)
        }
    }

    private fun setupColorWheel() {
        colorWheel.also {
            it.rgb = diagramColor
            it.colorChangeListener = this::onColorWheelColorChanged
        }
    }

    private fun onColorWheelColorChanged(rgb: Int) {
        alphaSeekBar.setAlphaRgb(rgb)
        updateSelectedColorViewBackground()
    }

    private fun setupAlphaSeekBar() {
        alphaSeekBar.apply {
            setAlphaArgb(diagramColor)
            setAlphaListener { _, _, _ -> updateSelectedColorViewBackground() }
        }
    }

    private fun updateSelectedColorViewBackground() {
        (selectedColorView.background as GradientDrawable).setColor(alphaSeekBar.currentColor)
    }

    private fun onApplyButtonClick() {
        diagramColor = alphaSeekBar.currentColor
        dismiss()
    }

    override fun onDismissed() {
        listener.onColorApplied(diagramColor)
    }

    interface Listener {

        fun onColorApplied(argb: Int)
    }
}

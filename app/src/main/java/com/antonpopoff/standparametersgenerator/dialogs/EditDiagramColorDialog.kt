package com.antonpopoff.standparametersgenerator.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.*
import com.antonpopoff.colorwheel.AlphaSeekBar
import com.antonpopoff.colorwheel.ColorWheel
import com.antonpopoff.colorwheel.utils.setAlpha
import com.antonpopoff.standparametersgenerator.R

class EditDiagramColorDialog(
        context: Context,
        private var diagramColor: Int,
        private val listener: Listener
) : BottomSheetDialog(context) {

    private lateinit var colorWheel: ColorWheel
    private lateinit var alphaSeekBar: AlphaSeekBar
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
        colorWheel.setColor(diagramColor)
        alphaSeekBar.setAlpha(Color.alpha(diagramColor))
    }

    private fun setupSelectedColorView() {
        selectedColorView.background = GradientDrawable().apply {
            cornerRadius = context.resources.getDimension(R.dimen.dialog_corners_radius)
            setColor(diagramColor)
        }
    }

    private fun setupColorWheel() {
        colorWheel.also {
            it.setColor(setAlpha(diagramColor, 255))
            it.colorChangeListener = this::onColorChanged
        }
    }

    private fun onColorChanged(rgb: Int) {
        alphaSeekBar.setOriginColor(rgb)
        updateSelectedColorViewBackground()
    }

    private fun setupAlphaSeekBar() {
        alphaSeekBar.apply {
            setOriginColor(diagramColor)
            setAlpha(Color.alpha(diagramColor))
            alphaChangeListener = { updateSelectedColorViewBackground() }
        }
    }

    private fun updateSelectedColorViewBackground() {
        val argb = setAlpha(colorWheel.argb, alphaSeekBar.colorAlpha)
        (selectedColorView.background as GradientDrawable).setColor(argb)
    }

    private fun onApplyButtonClick() {
        diagramColor = setAlpha(colorWheel.argb, alphaSeekBar.colorAlpha)
        dismiss()
    }

    override fun onDismissed() {
        listener.onColorApplied(diagramColor)
    }

    interface Listener {

        fun onColorApplied(argb: Int)
    }
}

package com.antonpopoff.standparametersgenerator.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.antonpopoff.colorwheel.utils.setAlpha
import com.antonpopoff.standparametersgenerator.R
import com.antonpopoff.standparametersgenerator.common.SwipeableDialog
import kotlinx.android.synthetic.main.dialog_fragment_edit_diagram_color.*

class EditDiagramColorDialog : SwipeableDialog() {

    private val initialColor by lazy { arguments?.getInt(KEY_INITIAL_COLOR, Color.WHITE) ?: Color.WHITE }

    override val layoutId = R.layout.dialog_fragment_edit_diagram_color

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.Base_Theme_AppCompat_Light_Dialog_Default)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetButton.setOnClickListener { resetColors() }
        applyButton.setOnClickListener { onApplyButtonClick() }
        cancelButton.setOnClickListener { dismiss() }
        setupSelectedColorView()
        setupColorWheel()
        setupAlphaSeekBar()
    }

    private fun resetColors() {
        colorWheel.setColor(initialColor)
        alphaSeekBar.setAlpha(Color.alpha(initialColor))
    }

    private fun setupSelectedColorView() {
        selectedColorView.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(R.dimen.dialog_corners_radius)
            setColor(initialColor)
        }
    }

    private fun setupColorWheel() {
        colorWheel.also {
            it.setColor(setAlpha(initialColor, 255))
            it.colorChangeListener = this::onColorChanged
        }
    }

    private fun onColorChanged(rgb: Int) {
        alphaSeekBar.setOriginColor(rgb)
        updateSelectedColorViewBackground()
    }

    private fun setupAlphaSeekBar() {
        alphaSeekBar.apply {
            setOriginColor(initialColor)
            setAlpha(Color.alpha(initialColor))
            alphaChangeListener = { updateSelectedColorViewBackground() }
        }
    }

    private fun updateSelectedColorViewBackground() {
        val argb = setAlpha(colorWheel.argb, alphaSeekBar.colorAlpha)
        (selectedColorView.background as GradientDrawable).setColor(argb)
    }

    private fun onApplyButtonClick() {
        val color = setAlpha(colorWheel.argb, alphaSeekBar.colorAlpha)

        (parentFragment as? Listener)?.onColorApplied(color)

        dismiss()
    }

    override fun onStart() {
        super.onStart()
        setupDialogWindow()
    }

    private fun setupDialogWindow() {
        dialog?.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }
    }

    interface Listener {

        fun onColorApplied(argb: Int)
    }

    companion object {

        private const val KEY_INITIAL_COLOR = "initial_color"

        fun create(initialColor: Int) = EditDiagramColorDialog().apply {
            arguments = Bundle().apply {
                putInt(KEY_INITIAL_COLOR, initialColor)
            }
        }
    }
}

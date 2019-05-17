package com.antonpopoff.standcharacteristicsgenerator.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.antonpopoff.colorwheel.utils.setAlpha
import com.antonpopoff.standcharacteristicsgenerator.R
import kotlinx.android.synthetic.main.dialog_fragment_edit_diagram_color.*

class EditDiagramColorDialog : DialogFragment() {

    private val initialColor by lazy { arguments?.getInt(KEY_INITIAL_COLOR, Color.WHITE) ?: Color.WHITE }

    private lateinit var selectedColorViewBackground: GradientDrawable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.Base_Theme_AppCompat_Light_Dialog_Default)
        selectedColorViewBackground = ContextCompat.getDrawable(context, R.drawable.rounded_corners_rectangle)?.mutate() as GradientDrawable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_edit_diagram_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetButton.setOnClickListener { colorWheel.setColor(initialColor) }
        applyButton.setOnClickListener { onApplyButtonClick() }
        cancelButton.setOnClickListener { dismiss() }
        setupSelectedColorView()
        setupColorWheel()
        setupAlphaSeekBar()
    }

    private fun setupSelectedColorView() {
        selectedColorViewBackground.setColor(initialColor)
        selectedColorView.background = selectedColorViewBackground
    }

    private fun setupColorWheel() {
        colorWheel.also {
            it.setColor(setAlpha(initialColor, 255))
            it.colorChangeListener = this::onColorChanged
        }
    }

    private fun onColorChanged(rgb: Int) {
        alphaSeekBar.color = rgb
        updateSelectedColorViewBackground()
    }

    private fun setupAlphaSeekBar() {
        alphaSeekBar.apply {
            color = initialColor
            setAlpha(Color.alpha(initialColor))
            alphaChangeListener = { updateSelectedColorViewBackground() }
        }
    }

    private fun updateSelectedColorViewBackground() {
        val argb = setAlpha(colorWheel.currentColorArgb, alphaSeekBar.colorAlpha)
        selectedColorViewBackground.setColor(argb)
    }

    private fun onApplyButtonClick() {
        val argb = colorWheel.currentColorArgb
        val alpha = alphaSeekBar.colorAlpha

        (parentFragment as? Listener)?.onColorApplied(setAlpha(argb, alpha))
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

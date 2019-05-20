package com.antonpopoff.standcharacteristicsgenerator.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.antonpopoff.colorwheel.utils.setAlpha
import com.antonpopoff.standcharacteristicsgenerator.R
import kotlinx.android.synthetic.main.dialog_fragment_edit_diagram_color.*

class EditDiagramColorDialog : DialogFragment() {

    private val initialColor by lazy { arguments?.getInt(KEY_INITIAL_COLOR, Color.WHITE) ?: Color.WHITE }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setStyle(STYLE_NORMAL, R.style.Base_Theme_AppCompat_Light_Dialog_Default)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_edit_diagram_color, container, false)
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
        val argb = colorWheel.argb
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

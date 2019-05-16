package com.antonpopoff.standcharacteristicsgenerator.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
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
        selectedColorView.background = selectedColorViewBackground
        cancelButton.setOnClickListener { dismiss() }
        resetButton.setOnClickListener { colorWheel.setColor(initialColor) }
        applyButton.setOnClickListener { onApplyButtonClick() }
        setupColorWheel()
    }

    private fun setupColorWheel() {
        colorWheel.apply {
            colorChangeListener = { selectedColorViewBackground.setColor(it) }
            setColor(initialColor)
        }
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

    private fun onApplyButtonClick() {
        (parentFragment as? Listener)?.onColorApplied(colorWheel.currentColorArgb)
        dismiss()
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

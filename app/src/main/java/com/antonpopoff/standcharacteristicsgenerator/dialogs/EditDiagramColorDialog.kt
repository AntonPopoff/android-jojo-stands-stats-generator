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
        setupColorWheel()
    }

    private fun setupColorWheel() {
        colorWheel.apply {
            colorChangeListener = { selectedColorViewBackground.setColor(it) }
            setColor(arguments?.getInt(KEY_COLOR, Color.WHITE) ?: Color.WHITE)
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

    companion object {

        private const val KEY_COLOR = "color"

        fun create(presetColor: Int) = EditDiagramColorDialog().apply {
            arguments = Bundle().apply {
                putInt(KEY_COLOR, presetColor)
            }
        }
    }
}

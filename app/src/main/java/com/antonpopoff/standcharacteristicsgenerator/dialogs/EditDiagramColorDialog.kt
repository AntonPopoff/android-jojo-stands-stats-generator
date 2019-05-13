package com.antonpopoff.standcharacteristicsgenerator.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.antonpopoff.standcharacteristicsgenerator.R

class EditDiagramColorDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_edit_diagram_color, container, false)
    }
}
package com.antonpopoff.standparametersgenerator.storage

import android.content.Context
import com.antonpopoff.standparametersview.diagram.ParameterName
import com.antonpopoff.standparametersview.diagram.ParameterRating
import com.antonpopoff.standparametersview.diagram.StandParameter
import com.antonpopoff.standparametersview.diagram.StandParameters

private const val PREFS_NAME = "app_preferences"
private const val KEY_COLOR = "diagram_color"

class AppDataPreferencesCache(context: Context): AppDataCache {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveDiagramColor(argb: Int) {
        preferences.edit().apply {
            putInt(KEY_COLOR, argb)
            apply()
        }
    }

    override fun readDiagramColor(defArgb: Int) = preferences.getInt(KEY_COLOR, defArgb)

    override fun saveStandRating(parameters: StandParameters) {
        preferences.edit().apply {
            parameters.parameters.forEach { putInt(it.name.name, it.rating.mark) }
            apply()
        }
    }

    override fun readStandRating(defParameters: StandParameters): StandParameters {
        val parameters = ParameterName.values.map {
            val parameterMark = preferences.getInt(it.name, ParameterRating.UNKNOWN.mark)
            val parameterRating = ParameterRating.valueOf(parameterMark) ?: ParameterRating.UNKNOWN
            val parameterName = ParameterName.valueOf(it.name)
            StandParameter(parameterName, parameterRating)
        }


        return StandParameters.Builder().setRatings(parameters).create()
    }
}

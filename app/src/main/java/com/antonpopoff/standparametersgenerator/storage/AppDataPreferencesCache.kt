package com.antonpopoff.standparametersgenerator.storage

import android.content.Context
import com.antonpopoff.standparametersgenerator.extensions.editAndApply
import com.antonpopoff.standparametersview.diagram.ParameterName
import com.antonpopoff.standparametersview.diagram.ParameterRating
import com.antonpopoff.standparametersview.diagram.StandParameters

private const val PREFS_NAME = "app_preferences"
private const val KEY_COLOR = "diagram_color"
private const val KEY_STAND_PARAMETERS = "stand_parameters"

fun StandParameters.toCompactString() = this.ratings.joinToString("") { it.mark.toString() }

class AppDataPreferencesCache(context: Context) : AppDataCache {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveDiagramColor(argb: Int) = preferences.editAndApply { putInt(KEY_COLOR, argb) }

    override fun readDiagramColor(defArgb: Int) = preferences.getInt(KEY_COLOR, defArgb)

    override fun saveStandRating(parameters: StandParameters) = preferences
            .editAndApply { putString(KEY_STAND_PARAMETERS, parameters.toCompactString()) }

    override fun readStandRating(defParameters: StandParameters): StandParameters {
        val parametersString = preferences.getString(KEY_STAND_PARAMETERS, null).orEmpty()

        if (parametersString.isEmpty()) return defParameters

        val ratings = parametersString
                .asSequence()
                .take(ParameterName.count)
                .map { it.toString().toIntOrNull() ?: ParameterRating.UNKNOWN.mark }
                .map { ParameterRating.valueOf(it) ?: ParameterRating.UNKNOWN }
                .toList()

        return StandParameters.from(ratings)
    }
}

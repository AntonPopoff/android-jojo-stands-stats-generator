package com.antonpopoff.standcharacteristicsgenerator.storage

import android.content.Context

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
}

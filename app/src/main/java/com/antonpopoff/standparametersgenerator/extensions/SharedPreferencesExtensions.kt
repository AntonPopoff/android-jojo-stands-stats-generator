package com.antonpopoff.standparametersgenerator.extensions

import android.content.SharedPreferences

fun SharedPreferences.editAndApply(block: SharedPreferences.Editor.() -> Unit) = this.edit().apply(block).apply()
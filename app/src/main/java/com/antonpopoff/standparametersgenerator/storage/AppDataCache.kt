package com.antonpopoff.standparametersgenerator.storage

import com.antonpopoff.standparametersview.diagram.StandParameters

interface AppDataCache {

    fun saveDiagramColor(argb: Int)

    fun readDiagramColor(defArgb: Int): Int

    fun saveStandRating(parameters: StandParameters)

    fun readStandRating(defParameters: StandParameters): StandParameters
}

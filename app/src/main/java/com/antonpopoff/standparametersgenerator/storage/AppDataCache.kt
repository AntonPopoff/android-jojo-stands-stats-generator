package com.antonpopoff.standcharacteristicsgenerator.storage

interface AppDataCache {

    fun saveDiagramColor(argb: Int)

    fun readDiagramColor(defArgb: Int): Int
}

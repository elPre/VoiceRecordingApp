package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

interface ShowLegendOnRecordings : OnRecordOrPlaybackHappening

interface OnRecordOrPlaybackHappening {
    fun setupLegend(legend: String)
}
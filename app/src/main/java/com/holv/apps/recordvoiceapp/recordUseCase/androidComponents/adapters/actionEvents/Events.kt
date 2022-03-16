package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents

import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio

sealed class Events {
    object Stop : Events()
    object Record : Events()
    object Pause : Events()
    object Play : Events()
    object PausePlayback : Events()
    data class PlayRecordedAudio (val recordAudio: RecordAudio) : Events()
    data class SeekBarAudio(val pos: Int) : Events()
    data class SeekBarReflectOnTimer(val pos: Int) : Events()
    object OpenSettings : Events()
}

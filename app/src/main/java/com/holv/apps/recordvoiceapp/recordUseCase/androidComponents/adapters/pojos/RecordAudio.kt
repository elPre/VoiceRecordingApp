package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos

data class RecordAudio(
    val name: String,
    val time: String,
    val duration: String,
    val size: String,
    val playbackFile: String,
    val action: RecordFileAction? = null)

enum class RecordFileAction {
    NO_SELECTION,
    SHARE,
    DELETE
}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

interface SetCountUpTimer : GetDurationFromAudio

interface GetDurationFromAudio {
    fun getAudioDuration(sec: Int)
}
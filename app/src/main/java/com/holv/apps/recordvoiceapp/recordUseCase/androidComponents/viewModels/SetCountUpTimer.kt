package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

interface SetCountUpTimer : SetCountTimeFromAudioDuration

interface SetCountTimeFromAudioDuration {
    fun setCountUpTimer(sec: Int)
}
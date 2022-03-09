package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.SeekBarMax
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.SetCountTimeFromAudioDuration

interface PlayRecording: Playback,
    StopPlayback,
    PausePlayback,
    SetListenerDuration,
    SetListenerSeconds

interface Playback {
    fun playRecording(infoRecording: InfoRecording)
}

interface StopPlayback {
    fun stopPlayBack()
}

interface PausePlayback {
    fun pausePlayback()
}

interface SetListenerDuration {
    fun setListener(listenerDuration : SeekBarMax)
}

interface SetListenerSeconds {
    fun setListenerSeconds(secondsDuration: SetCountTimeFromAudioDuration)
}
package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.SeekBarMax
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.GetDurationFromAudio

interface PlayRecording: Playback,
    StopPlayback,
    PausePlayback,
    SetListenerDuration,
    SetListenerSeconds,
    GetAudioCurrentTime,
    SeekToSpecificPosition,
    GetDurationPlayback

interface Playback {
    fun playRecording(infoRecording: InfoRecording)
}

interface StopPlayback {
    fun stopPlayBack()
}

interface PausePlayback {
    fun pausePlayback()
    fun seekWhilePause(pos: Int)
}

interface SetListenerDuration {
    fun setListener(listenerDuration : SeekBarMax)
}

interface SetListenerSeconds {
    fun setListenerSeconds(secondsDuration: GetDurationFromAudio)
}

interface GetAudioCurrentTime {
    fun onGetAudioCurrentTime(): Int
}

interface SeekToSpecificPosition {
    fun onSeekToSpecificPos(pos: Int)
}

interface GetDurationPlayback {
    fun getDurationPlayback(pathToFile: String) : String
}
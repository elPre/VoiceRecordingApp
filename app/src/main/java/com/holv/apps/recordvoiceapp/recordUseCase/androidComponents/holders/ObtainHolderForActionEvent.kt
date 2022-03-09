package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

interface ObtainHolderForActionEvent: UpdateTickClock, SeekBarMax, FinishPlayback


interface FireAnimation {
    fun onFireAnimation(isTurn: Boolean)
}

interface UpdateTickClock {
    fun onClockTick(msg: String)
}

interface SeekBarMax {
    fun setMaxSeekBar(maxString: String, maxInt: Int)
}

interface StopPlayback {
    fun stopPlayback()
}

interface FinishPlayback {
    fun onFinishPlayback()
}
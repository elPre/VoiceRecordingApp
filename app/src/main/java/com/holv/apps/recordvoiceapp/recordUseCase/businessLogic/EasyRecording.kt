package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

interface EasyRecording : StartRecording, StopRecording, PauseRecording, ResumeRecording

interface StartRecording {
    fun startRecording(recordSettings: RecordSettings)
}

interface StopRecording {
    fun stopRecording()
}

interface PauseRecording {
    fun pauseRecording()
}

interface ResumeRecording {
    fun resumeRecording()
}

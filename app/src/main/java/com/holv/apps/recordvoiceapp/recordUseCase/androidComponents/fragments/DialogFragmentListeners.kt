package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio

interface DialogFragmentListeners : OnMp3ConversionSuccess, OnConversionFailed, SetProgressBar

interface OnMp3ConversionSuccess {
    fun onConversionSuccess()
}

interface OnConversionFailed {
    fun onConversionFailed()
}

interface SetProgressBar {
    fun onSetProgressDone(progress: Int)
}

interface DialogOnDelete : DeleteRecording

interface DeleteRecording {
    fun onDeleteRecording(adapterPos:Int, audio: RecordAudio)
}
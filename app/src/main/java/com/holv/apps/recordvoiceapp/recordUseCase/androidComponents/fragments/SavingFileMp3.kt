package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import java.io.Serializable

interface SavingFileMp3 : Serializable, SaveFile, CancelSaveRecording

interface SaveFile {
    fun onSaveFile(fileName: String, listeners: DialogFragmentListeners)
}

interface CancelSaveRecording {
    fun onCancelSave()
}
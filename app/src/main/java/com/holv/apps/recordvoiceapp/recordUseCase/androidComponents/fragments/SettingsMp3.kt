package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments

import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordSettings
import java.io.Serializable

interface SettingsMp3 : Serializable, SaveSettings

interface SaveSettings {
    fun saveSettings(recordSettings: RecordSettings)
}


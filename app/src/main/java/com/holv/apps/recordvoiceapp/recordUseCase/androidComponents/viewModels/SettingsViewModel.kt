package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModel(val app: Application) : ViewModel() {


    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SettingsViewModel(app) as T
        }
    }
}
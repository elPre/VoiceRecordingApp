package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents

sealed class Events {
    object Stop : Events()
    object Record : Events()
    object Pause : Events()
    object Play : Events()
}

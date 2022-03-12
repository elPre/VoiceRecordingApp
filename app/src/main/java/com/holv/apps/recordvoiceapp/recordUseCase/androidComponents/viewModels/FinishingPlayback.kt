package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import kotlinx.coroutines.Job

interface FinishingPlayback {
    fun onFinishPlayback() : Job
}
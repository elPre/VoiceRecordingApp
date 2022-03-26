package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.app.Application

data class InfoRecording(val path: String)

data class InfoCovertToMp3(val app: Application, val fileName: String, val recordType: RecordType)
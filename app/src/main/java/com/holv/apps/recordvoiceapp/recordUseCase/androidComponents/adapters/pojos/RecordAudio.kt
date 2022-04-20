package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordAudio(
    val name: String,
    val time: String,
    val duration: String,
    val size: String,
    val playbackFile: String,
    val contentUri: Uri? = null,
    val id: Long?  = null) : Parcelable
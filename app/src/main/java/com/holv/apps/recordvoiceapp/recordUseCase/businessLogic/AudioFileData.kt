package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.net.Uri
import java.io.File
import java.util.*

data class AudioFileData(
    val id: Long,
    val uri: Uri ? = null,
    val name: String,
    val duration: String,
    val sizeFile: String,
    val date: Date,
    val fileAudio: File? = null,
    val albumName: String? = null,
    val dataPlayback: String? = null,
    val contentUri: Uri? = null)

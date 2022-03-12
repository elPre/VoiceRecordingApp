package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.net.Uri
import java.io.File

data class AudioFileData(
    val id: Long,
    val uri: Uri,
    val name:String,
    val duration: String,
    val sizeFile: String,
    val fileAudio: File? = null)

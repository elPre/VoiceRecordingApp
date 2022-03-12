package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

interface FileLogic : SaveFile, QueryFiles, DeleteFile

interface SaveFile {
    fun saveFile(data : AudioFileData)
}

interface QueryFiles {
    fun queryFiles() : List<AudioFileData>
}

interface DeleteFile {
    fun deleteFile(data : AudioFileData)
}
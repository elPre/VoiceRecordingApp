package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.provider.MediaStore
import android.util.Log
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.sdk29AndUp
import java.io.File
import java.util.*

class FileManager(val app: Application) : FileLogic {

    override fun saveFile(data: AudioFileData) : Boolean {
        try {
            val audioCollection = sdk29AndUp {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Audio.Media._ID, data.id)
                put(MediaStore.Audio.Media.DISPLAY_NAME, "${data.name}.mp3")
                put(MediaStore.Audio.AudioColumns.DATA, "${data.name}.mp3")
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                put(MediaStore.Audio.Media.ALBUM, ALBUM_NAME)
                put(MediaStore.Audio.Media.DURATION, data.duration)
                put(MediaStore.Audio.Media.SIZE, data.sizeFile)
                put(MediaStore.Audio.Media.DATE_ADDED, data.date.time)

            }

            app.contentResolver.insert(audioCollection, contentValues)

            app.contentResolver.insert(audioCollection, contentValues)?.also { uri ->
                app.contentResolver.openOutputStream(uri).use { outputStream ->
                    outputStream?.write(data.fileAudio?.readBytes())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"there was a error ${e.message}")
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun queryFiles() : List<AudioFileData> {
        val list = mutableListOf<AudioFileData>()
        try {
            val audioCollection = sdk29AndUp {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED
            )
            /*
            app.contentResolver.query(
                audioCollection,
                projection,
                MediaStore.Audio.Media.ALBUM + " = ?",
                arrayOf(ALBUM_NAME),
                "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            )
             */

            app.contentResolver.query(
                audioCollection,
                projection,
                null,
                null,
                "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateColumn =cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(nameColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getString(durationColumn)
                    val size = cursor.getString(sizeColumn)
                    val dateAdded = cursor.getLong(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    list.add(
                        AudioFileData(
                            id = id,
                            uri = contentUri,
                            name = displayName,
                            duration =  duration,
                            sizeFile = size,
                            date = Date(dateAdded)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"there was a error ${e.message}")
            e.printStackTrace()
            emptyList<AudioFileData>()
        }

        return list
    }

    override fun deleteFile(data : AudioFileData) : Boolean {
        try {
            data.uri?.let { app.contentResolver.delete(it, null, null) }
        } catch (e: SecurityException) {
            return false
        }
        return true
    }

    companion object {
        const val TAG = "FileManager"
        private const val ALBUM_NAME = "VoiceRecordAppAlbum1"
    }

}
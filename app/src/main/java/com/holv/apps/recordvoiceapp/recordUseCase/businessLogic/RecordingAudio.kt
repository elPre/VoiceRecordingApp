package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.app.Application
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.IOException

class RecordingAudio(val app: Application): EasyRecording {

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

    override fun startRecording(recordSettings: RecordSettings) {
        val downloadFolder = app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        fileName = "$downloadFolder/$TMP_FILE_M4A_NAME"
        // need to get the info that comes  in the recordingSettings
        // and set it into the MediaRecorder object
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
            setAudioSamplingRate(recordSettings.recordQuality.sampleRate)
            setAudioEncodingBitRate(recordSettings.recordQuality.biteRate)
            setMaxDuration(-1)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("RecordingAudio", "prepare() failed")
            }

            start()
        }
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun pauseRecording() {
        recorder?.apply {
            pause()
        }
    }

    override fun resumeRecording() {
        recorder?.apply {
            resume()
        }
    }

    companion object {
        const val TMP_FILE_M4A_NAME = "audiorecord.m4a"
    }

}
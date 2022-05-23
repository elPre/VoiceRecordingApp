package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

import android.app.Application
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.SeekBarMax
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.StopPlayback
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels.GetDurationFromAudio
import java.io.IOException

class PlayAudio(val app: Application) : PlayRecording, StopPlayback {

    private var fileName: String = ""
    private var player: MediaPlayer? = null
    private var listenerDuration: SeekBarMax? = null
    private var listenerCountUpTime: GetDurationFromAudio? = null
    private var pausePlayback: Int? = 0

    private val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }

    private val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }

    override fun playRecording(infoRecording: InfoRecording) {

        val downloadFolder = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        fileName = infoRecording.path.ifEmpty { "$downloadFolder/$TMP_FILE_M4A_NAME" }
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
                listenerDuration?.setMaxSeekBar(timeInString(seconds), seconds)
                listenerCountUpTime?.getAudioDuration(seconds)

            } catch (e: IOException) {
                Log.e("PlayAudio", "message -> ${e.message}")
                e.printStackTrace()
            }
        }
        if (pausePlayback != null && pausePlayback!! > 0) {
            val seekToPos = pausePlayback!! + 1
            player?.seekTo(seekToPos * 1000)
            pausePlayback = 0
        }

    }

    override fun stopPlayBack() {
        pausePlayback = 0
        player?.release()
        player = null
        Log.d("PlayAudio","stopPlayBack $pausePlayback")
    }

    override fun pausePlayback() {
        Log.d("PlayAudio","pausePlayback")
        pausePlayback = player?.currentSeconds
        player?.pause()
    }

    override fun seekWhilePause(pos: Int) {
        pausePlayback = pos
        Log.d("PlayAudio","seekWhilePause $pausePlayback")
    }

    override fun setListener(listenerDuration: SeekBarMax) {
        this.listenerDuration = listenerDuration
    }

    override fun setListenerSeconds(secondsDuration: GetDurationFromAudio) {
        this.listenerCountUpTime = secondsDuration
    }

    override fun onGetAudioCurrentTime(): Int {
        return player?.currentSeconds ?: 0
    }

    override fun onSeekToSpecificPos(pos: Int) {
        player?.seekTo(pos * 1000)
    }

    override fun stopPlayback() {
        stopPlayBack()
    }

    override fun getDurationPlayback(pathToFile: String): String {
        var duration: String? = null
        player = MediaPlayer().apply {
            try {
                setDataSource(pathToFile)
                prepare()
                duration = timeInString(seconds)
            } catch (e: IOException) {
                Log.e("PlayAudio", "prepare() failed  -> $pathToFile")
            }
        }
        player?.release()
        player = null
        return "$duration secs" ?: timeInString(0)
    }

    override fun playbackFromNotification() {
        Log.d("PlayAudio","playbackFromNotification $player")
        player?.reset()
        player?.apply {
            try {
                Log.d("PlayAudio","playbackFromNotification $fileName")
                setDataSource(fileName)
                prepare()
                start()
                listenerDuration?.setMaxSeekBar(timeInString(seconds), seconds)
                listenerCountUpTime?.getAudioDuration(seconds)

            } catch (e: IOException) {
                Log.e("PlayAudio", "message -> ${e.message}")
                e.printStackTrace()
            }
        }
        if (pausePlayback != null && pausePlayback!! > 0) {
            val seekToPos = pausePlayback!! + 1
            player?.seekTo(seekToPos * 1000)
            pausePlayback = 0
        }
    }

    private fun timeInString(seconds: Int): String {
        return String.format(
            "%02d:%02d",
            (seconds / 3600 * 60 + ((seconds % 3600) / 60)),
            (seconds % 60)
        )
    }

    companion object {
        const val TMP_FILE_M4A_NAME = "audiorecord.m4a"
    }

}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.*
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordFileAction
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.FireAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.ObtainHolderForActionEvent
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.*
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class RecordViewModel(val app: Application) : ViewModel(), SetCountUpTimer, FinishingPlayback {

    //Business objects
    private val recordAudio: EasyRecording = RecordingAudio(app)
    private val playAudio: PlayRecording = PlayAudio(app)
    private val ffmpeg: Mp3Converter = Mp3ClassConverter()
    private val clockTimer = ClockTimer()

    private var totalSecondsToPlayback = 0L
    private var isPlaying = AtomicBoolean(false)
    private var isPlaybackPause = AtomicBoolean(false)

    //listeners that lives on the holders
    private lateinit var listener: ObtainHolderForActionEvent
    private lateinit var fireAnimationListener: FireAnimation

    //Load the whole page that is a RecyclerView
    val moduleItem = MutableLiveData<List<RecordItem>>()

    //time stamp to save of each recording
    private var timeStamp: Date? = null

    //Pause Recording
    private var isRecordingPause = AtomicBoolean(false)

    //seek while pause
    private var seekWhilePausePlayback = 0

    override fun getAudioDuration(sec: Int) {
        totalSecondsToPlayback = sec.toLong()
        clockTimer.totalSecondsToPlayback = sec.toLong()
    }

    fun loadPage(isNewFragment: Boolean) = viewModelScope.launch {
        if (isNewFragment) {
            load()
        }
    }

    private fun load() {
        val list = mutableListOf<RecordItem>()
        list.add(TopBanner)
        list.add(LogoAnimation)
        list.add(UserControls)
        list.add(LegendRecordings)
        val listRecordings = mutableListOf<Records>()
        val downloadFolder = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val fileName = "$downloadFolder/audiorecord.m4a"
        for (i in 0 until 100) {
            listRecordings.add(
                Records(
                    RecordAudio(
                        name = "Amazing time at Bungalow $i let addd more text to see what happens",
                        time = "Fri Feb 18 2022",
                        duration = "$i : $i mins",
                        size = "$i Mb",
                        playbackFile = fileName,
                        RecordFileAction.NO_SELECTION
                    )
                )
            )
        }
        list.addAll(listRecordings)
        moduleItem.postValue(list.toList())
    }


    fun starRecording() = viewModelScope.launch {
        timeStamp = Date()
        clockTimer.countDownTimer.start()
        if (isRecordingPause.get()) {
            recordAudio.resumeRecording()
            isRecordingPause.set(false)
        } else {
            recordAudio.startRecording(RecordSettings(RecordType.MP3_HIGHEST))
        }
    }

    fun stopRecording() = viewModelScope.launch {
        recordAudio.stopRecording()
        //ffmpeg.convertToMp3("","", app)
        clockTimer.countDownTimer.cancel()
    }

    fun pauseRecording() = viewModelScope.launch {
        recordAudio.pauseRecording()
        isRecordingPause.set(true)
        clockTimer.countDownTimer.cancel()
    }

    fun startPlayback() = viewModelScope.launch {
        if (isPlaying.get().not()) {
            if (isPlaybackPause.get()) {
                playAudio.seekWhilePause(seekWhilePausePlayback)
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(""))
            isPlaying.set(true)
            clockTimer.isPlaying.set(true)
            getAudioCurrentSeconds()
        }
    }

    fun startPlaybackFromRecordings(playbackData: RecordAudio) = viewModelScope.launch {
        if (isPlaying.get().not()) {
            if (isPlaybackPause.get()) {
                playAudio.seekWhilePause(seekWhilePausePlayback)
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(playbackData.playbackFile))
            isPlaying.set(true)
            clockTimer.isPlaying.set(true)
            getAudioCurrentSeconds()
        }
    }

    private fun getAudioCurrentSeconds() = viewModelScope.launch {
        while (isPlaying.get()) {
            listener.updateSeekBar(playAudio.onGetAudioCurrentTime())
            delay(HALF_SECOND)
        }
    }

    fun stopPlayback() = viewModelScope.launch {
        isPlaying.set(false)
        playAudio.stopPlayBack()
        //Also need the time stamp
        clockTimer.resetTimerVariables()
        listener.onClockTick("00:00")
        listener.updateSeekBar(0)
    }

    override fun onFinishPlayback() = viewModelScope.launch {
        isPlaying.set(false)
        clockTimer.isPlaying.set(false)
        playAudio.stopPlayBack()
        clockTimer.resetTimerVariables()
        listener.onClockTick("00:00")
    }

    fun pausePlayback() = viewModelScope.launch {
        playAudio.pausePlayback()
        isPlaying.set(false)
        clockTimer.isPlaying.set(false)
        isPlaybackPause.set(true)
    }


    fun setSeekBarPos(pos: Int) = viewModelScope.launch {
        Log.d("RecordViewModel","setSeekBarPos -> $pos")
        playAudio.onSeekToSpecificPos(pos)
        listener.updateSeekBar(pos)
        clockTimer.resetTimerVariables()
        val isMoreThanHour = pos >= 3600
        if (isMoreThanHour) {
            val hr = pos / ONE_HOUR_IN_SECS
            val tmpMis = pos % ONE_HOUR_IN_SECS
            val mins = tmpMis / 60
            val sec = tmpMis % 60
            clockTimer.seconds.set(sec.toLong())
            clockTimer.minutes.set(mins.toLong())
            clockTimer.hours.set(hr.toLong())
        } else {
            val mins = pos / 60
            val sec = pos % 60
            clockTimer.seconds.set(sec.toLong())
            clockTimer.minutes.set(mins.toLong())
        }
    }

    fun setSeekBarPosUpdateTimer(pos: Int) = viewModelScope.launch {
        seekWhilePausePlayback = pos
        var showTimer = ""
        val isMoreThanHour = pos >= 3600
        if (isMoreThanHour) {
            val hr = pos / ONE_HOUR_IN_SECS
            val tmpMis = pos % ONE_HOUR_IN_SECS
            val mins = tmpMis / 60
            val sec = tmpMis % 60
            val secs = if (sec > 9) "$sec" else "0$sec"
            val min = if (mins > 9) "$mins" else "0$mins"
            val hrs = if (hr > 9) "$hr" else "0$hr"
            showTimer = "$hrs:$min:$secs"
        } else {
            val mins = pos / 60
            val sec = pos % 60
            val secs = if (sec > 9) "$sec" else "0$sec"
            val min = if (mins > 9) "$mins" else "0$mins"
            showTimer = "$min:$secs"
        }
        listener.onClockTick(showTimer)
        if(pos >= totalSecondsToPlayback) {
            fireAnimationListener.onFireAnimation(false)
            listener.onFinishPlayback()
            isPlaying.set(false)
            clockTimer.isPlaying.set(false)
            playAudio.stopPlayBack()
            clockTimer.resetTimerVariables()
        }
    }

    fun animationOnOff(onOff: Boolean) = viewModelScope.launch {
        fireAnimationListener.onFireAnimation(onOff)
    }

    fun setListenerForHolders(recyclerView: RecyclerView) = viewModelScope.launch {
        val itemCount = recyclerView.adapter?.itemCount
        if (itemCount != null && itemCount > 0) {
            val animationListener = recyclerView.findViewHolderForAdapterPosition(ANIMATION_HOLDER)
            val userControlsListener = recyclerView.findViewHolderForAdapterPosition(USER_CONTROLS_HOLDER)
            if (animationListener is FireAnimation) {
                fireAnimationListener = animationListener
                clockTimer.fireAnimationListener = fireAnimationListener
            }
            if (userControlsListener is ObtainHolderForActionEvent) {
                playAudio.setListener(userControlsListener)
                listener = userControlsListener
                clockTimer.listener = listener
            }
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecordViewModel(app) as T
        }
    }

    companion object {
        private const val ANIMATION_HOLDER = 1
        private const val USER_CONTROLS_HOLDER = 2
        private const val HALF_SECOND = 500L
        private const val ONE_HOUR_IN_SECS = 3600
    }

}
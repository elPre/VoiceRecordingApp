package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import android.os.CountDownTimer
import android.os.Environment
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
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class RecordViewModel(val app: Application) : ViewModel(), SetCountUpTimer {

    //Business objects
    private val recordAudio: EasyRecording = RecordingAudio(app)
    private val playAudio: PlayRecording = PlayAudio(app)
    private val ffmpeg: Mp3Converter = Mp3ClassConverter()

    //CountDownTimer
    private var totalSeconds: Long = 36000 * 10 //  ten hours of recording in seconds
    private val intervalSeconds: Long = 1 // one second ticking
    private var timeHolder =  AtomicLong(0)
    private var seconds = AtomicLong(0)
    private var minutes = AtomicLong(0)
    private var hours = AtomicLong(0)
    private var isPlaying = AtomicBoolean(false)
    private var isPlaybackPause = AtomicBoolean(false)
    private var totalSecondsToPlayback = 0L

    //listeners that lives  on  the  holders
    private lateinit var listener: ObtainHolderForActionEvent
    private lateinit var fireAnimationListener: FireAnimation

    //Load the whole page that is a RecyclerView
    val moduleItem = MutableLiveData<List<RecordItem>>()

    //time stamp to save of each recording
    private var timeStamp: Date? = null

    //Pause Recording
    private var isRecordingPause = AtomicBoolean(false)

    override fun setCountUpTimer(sec: Int) {
        totalSecondsToPlayback = sec.toLong()
        clockTickTime()
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
        val downloadFolder = app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
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
        countDownTimer.start()
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
    }

    fun pauseRecording() = viewModelScope.launch {
        recordAudio.pauseRecording()
        isRecordingPause.set(true)
        countDownTimer.cancel()
    }

    fun startPlayback() = viewModelScope.launch {
        if (isPlaying.get().not()) {
            if (isPlaybackPause.get().not()) {
                seconds.set(0)
                timeHolder.set(0)
            } else {
                seconds.incrementAndGet()
                timeHolder.incrementAndGet()
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(""))
            isPlaying.set(true)
        }
    }

    fun startPlaybackFromRecordings(playbackData: RecordAudio) = viewModelScope.launch {
        if (isPlaying.get().not()) {
            if (isPlaybackPause.get().not()) {
                seconds.set(0)
                timeHolder.set(0)
            } else {
                seconds.incrementAndGet()
                timeHolder.incrementAndGet()
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(playbackData.playbackFile))
            isPlaying.set(true)
        }
    }

    fun stopPlayback() = viewModelScope.launch {
        isPlaying.set(false)
        playAudio.stopPlayBack()
        Log.d("RecordViewModel", "Reset variable and show DialogFragment that accepts the name")
        //Also need the time stamp
        seconds.set(0)
        timeHolder.set(0)
        minutes.set(0)
        hours.set(0)
        listener.onClockTick("00:00")
        countDownTimer.cancel()
    }

    fun finishPlayback() = viewModelScope.launch {
        isPlaying.set(false)
        playAudio.stopPlayBack()
        seconds.set(0)
        timeHolder.set(0)
        minutes.set(0)
        hours.set(0)
        listener.onClockTick("00:00")
    }

    fun pausePlayback() = viewModelScope.launch {
        playAudio.pausePlayback()
        countDownTimer.cancel()
        isPlaying.set(false)
        isPlaybackPause.set(true)
    }

    private fun clockTickTime() = viewModelScope.launch {
        countDownTimer.start()
    }

    private val countDownTimer =
        object : CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            override fun onTick(millisUntilFinished: Long) {

                if (seconds.get() == 0L) {
                    listener.onClockTick("00:00")
                    timeHolder.incrementAndGet()
                    seconds.incrementAndGet()
                } else {

                    if (minutes.get() > 59) {
                        seconds.set(0)
                        minutes.set(0)
                        hours.incrementAndGet()
                    }
                    if (seconds.get() > 0 && seconds.get().mod(60) == 0) {
                        minutes.incrementAndGet()
                        seconds.set(0)
                    } else if (seconds.get() > 59) {
                        seconds.set(seconds.get().mod(60).toLong())
                    }

                    val secs = if (seconds.get() > 9) "$seconds" else "0$seconds"
                    val min = if (minutes.get() > 9) "$minutes" else "0$minutes"
                    val hrs = if (hours.get() > 9) "$hours" else "0$hours"

                    if (isPlaying.get()) {
                        if (seconds.get() >= totalSecondsToPlayback || timeHolder.get() >= totalSecondsToPlayback) {
                            isPlaying.set(false)
                            onFinish()
                            cancel()
                            finishPlayback()
                            listener.onFinishPlayback()
                        }
                    }

                    if (hours.get() > 0) {
                        //Log.d("RecordViewModel","$hrs:$min:$secs")
                        listener.onClockTick("$hrs:$min:$secs")
                    } else {
                        //Log.d("RecordViewModel","$min:$secs")
                        listener.onClockTick("$min:$secs")
                    }
                    timeHolder.incrementAndGet()
                    seconds.incrementAndGet()
                }
            }

            override fun onFinish() {
                fireAnimationListener.onFireAnimation(isTurn = false)
            }

        }

    fun setListenerForHolders(recyclerView: RecyclerView) = viewModelScope.launch {

        val itemCount = recyclerView.adapter?.itemCount

        if (itemCount != null && itemCount > 0) {

            val animationListener = recyclerView.findViewHolderForAdapterPosition(ANIMATION_HOLDER)
            val userControlsListener = recyclerView.findViewHolderForAdapterPosition(USER_CONTROLS_HOLDER)

            if (animationListener is FireAnimation) {
                fireAnimationListener = animationListener
            }

            if (userControlsListener is ObtainHolderForActionEvent) {
                playAudio.setListener(userControlsListener)
                listener = userControlsListener
            }
        }
    }

    fun animationOnOff(onOff: Boolean) = viewModelScope.launch {
        fireAnimationListener.onFireAnimation(onOff)
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
    }

}
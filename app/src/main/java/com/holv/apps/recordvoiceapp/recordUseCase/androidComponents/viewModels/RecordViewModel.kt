package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.activities.MainActivity
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.*
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments.DialogFragmentListeners
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.fragments.SettingsMp3
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.FireAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.ObtainHolderForActionEvent
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.NotificationUtils
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.*
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordType
import com.holv.apps.recordvoiceapp.recordUseCase.proto.ProtoRepository
import com.holv.apps.recordvoiceapp.recordUseCase.proto.RecordSettingsOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class RecordViewModel(val app: Application) : ViewModel(),
    SetCountUpTimer,
    SettingsMp3 {

    //Business objects
    private val recordAudio: EasyRecording = RecordingAudio(app)
    private val playAudio: PlayRecording = PlayAudio(app)
    private val clockTimer = ClockTimer()

    private var totalSecondsToPlayback = AtomicLong(0)
    private var isPlaying = AtomicBoolean(false)
    private var isPlaybackPause = AtomicBoolean(false)

    //listeners that lives on the holders
    private lateinit var listener: ObtainHolderForActionEvent
    private lateinit var fireAnimationListener: FireAnimation

    //Load the whole page that is a RecyclerView
    val moduleItem = MutableLiveData<MutableList<RecordItem>>()
    val wasItemAdd : MutableLiveData<Pair<Boolean, MutableList<RecordItem>>> = MutableLiveData(Pair(false, mutableListOf()))
    val shareLiveData = MutableLiveData<Intent>()

    //time stamp to save of each recording
    private var timeStamp: Date? = null

    //Pause Recording
    private var isRecordingPause = AtomicBoolean(false)

    //should show dialog fragment to save  file
    var startRecording = AtomicBoolean(false)

    //seek while pause
    private var seekWhilePausePlayback = 0

    //loop the playback
    private var isLooperOn = AtomicBoolean(false)

    //loop all the play list
    private var loopAllThePlayList = AtomicBoolean(false)

    //current audio to play back
    private var audioPlayback: String = ""

    //the list of recordings
    private var listOfRecordings = listOf<AudioFileData>()

    //List that hold all  the holders
    private val list = mutableListOf<RecordItem>()

    //Protobuf for saving MP3 settings
    private val protoBuffersRepo = ProtoRepository(app)

    //Current audio playback notification
    private var audioNamePlayback = ""
    private var audioFilePlayback = ""

    fun loadPage(isNewFragment: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        if (isNewFragment) {
            load()
        }
    }

    private fun load() {
        list.add(TopBanner)
        list.add(LogoAnimation)
        list.add(UserControls)
        list.add(LegendRecordings)
        list.addAll(queryInfoToLoadRecyclerView())
        moduleItem.postValue(list)
    }

    private fun loadWhenCreateOneRecording() {
        list.clear()
        list.add(TopBanner)
        list.add(LogoAnimation)
        list.add(UserControls)
        list.add(LegendRecordings)
        list.addAll(queryInfoToLoadRecyclerView())
    }

    fun starRecording() = viewModelScope.launch(Dispatchers.IO) {
        checkIfRecordWasPaused()
        listener.showHideSeekBar(show = false)
        startRecording.set(true)
        timeStamp = Date()
        clockTimer.countDownTimer.start()
        if (isRecordingPause.get()) {
            recordAudio.resumeRecording()
            isRecordingPause.set(false)
        } else {
            //get from the preferences data store the record type
            NotificationUtils.showRecordingNotification(app.applicationContext, "Recording message", "Here should be  a great message", MainActivity::class.java)
            val recordSettings = getMp3Settings()
            recordAudio.startRecording(recordSettings)
        }
    }

    fun stopRecording() = viewModelScope.launch(Dispatchers.IO) {
        NotificationUtils.clearNotifications(app)
        recordAudio.stopRecording()
        clockTimer.countDownTimer.cancel()
    }

    fun pauseRecording() = viewModelScope.launch(Dispatchers.IO) {
        recordAudio.pauseRecording()
        isRecordingPause.set(true)
        clockTimer.countDownTimer.cancel()
    }

    fun startPlayback() = viewModelScope.launch(Dispatchers.IO) {
        startRecording.set(false)
        if (isPlaying.get().not()) {
            if (isPlaybackPause.get()) {
                playAudio.seekWhilePause(seekWhilePausePlayback)
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(audioPlayback))
            isPlaying.set(true)
            clockTimer.isPlaying.set(true)
            getAudioCurrentSeconds()
        }
    }

    fun startPlaybackFromRecordings(playbackData: RecordAudio) = viewModelScope.launch(Dispatchers.IO) {
        listener.showHideSeekBar(show = true)
        startRecording.set(false)
        if (isPlaying.get().not()) {
            audioNamePlayback = playbackData.name
            audioFilePlayback = playbackData.playbackFile
            if (isPlaybackPause.get()) {
                playAudio.seekWhilePause(seekWhilePausePlayback)
            }
            playAudio.setListenerSeconds(this@RecordViewModel)
            playAudio.playRecording(InfoRecording(playbackData.playbackFile))
            isPlaying.set(true)
            clockTimer.isPlaying.set(true)
            getAudioCurrentSeconds()
            NotificationUtils.showPlaybackNotification(app.applicationContext, audioNamePlayback, "Here should be  a great message", isPlayback = true)
        }
    }

    fun playbackFromNotification() = viewModelScope.launch(Dispatchers.IO) {
        listener.showHideSeekBar(show = true)
        startRecording.set(false)
        isPlaying.set(true)
        playAudio.setListenerSeconds(this@RecordViewModel)
        getAudioCurrentSeconds()
        playAudio.playbackFromNotification()
        NotificationUtils.showPlaybackNotification(app.applicationContext, audioNamePlayback, "Here should be  a great message", isPlayback = true)
    }

    private fun getAudioCurrentSeconds() = viewModelScope.launch(Dispatchers.IO) {
        while (isPlaying.get()) {
            listener.updateSeekBar(playAudio.onGetAudioCurrentTime())
            delay(HALF_SECOND)
        }
    }

    override fun getAudioDuration(sec: Int) {
        totalSecondsToPlayback.set(sec.toLong())
        clockTimer.totalSecondsToPlayback = sec.toLong()
    }

    fun stopPlayback() = viewModelScope.launch(Dispatchers.IO) {
        isPlaying.set(false)
        playAudio.stopPlayBack()
        clockTimer.resetTimerVariables()
        listener.onClockTick("00:00")
        listener.updateSeekBar(0)
    }

    fun checkIfRecordWasPaused() = viewModelScope.launch(Dispatchers.IO) {
        if (isPlaying.get()) {
            stopPlayback()
            isPlaying.set(false)
        } else {
            if (isRecordingPause.get().not()) {
                stopPlayback()
            }
        }
    }

//    override fun onFinishPlayback() = viewModelScope.launch(Dispatchers.IO) {
//        isPlaying.set(false)
//        clockTimer.isPlaying.set(false)
//        playAudio.stopPlayBack()
//        clockTimer.resetTimerVariables()
//        listener.onClockTick("00:00")
//    }

    fun pausePlayback() = viewModelScope.launch(Dispatchers.IO) {
        isPlaying.set(false)
        playAudio.pausePlayback()
        clockTimer.isPlaying.set(false)
        isPlaybackPause.set(true)
        NotificationUtils.showPlaybackNotification(app.applicationContext, audioNamePlayback, "Here should be  a great message", isPlayback = false)
    }


    fun setSeekBarPos(pos: Int) = viewModelScope.launch(Dispatchers.IO) {
        playAudio.onSeekToSpecificPos(pos)
        listener.updateSeekBar(pos)
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

    fun setSeekBarPosUpdateTimer(pos: Int) = viewModelScope.launch(Dispatchers.IO) {
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
        if (pos >= totalSecondsToPlayback.get()) {
            fireAnimationListener.onFireAnimation(false)
            listener.onFinishPlayback()
            isPlaying.set(false)
            clockTimer.isPlaying.set(false)
            playAudio.stopPlayBack()
            clockTimer.resetTimerVariables()
            if (isLooperOn.get()) {
                startPlayback()
            } else {
                NotificationUtils.clearNotifications(app.applicationContext)
            }
        }
    }

    fun animationOnOff(onOff: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        fireAnimationListener.onFireAnimation(onOff)
    }

    fun setListenerForHolders(recyclerView: RecyclerView) = viewModelScope.launch(Dispatchers.IO) {
        val itemCount = recyclerView.adapter?.itemCount
        if (itemCount != null && itemCount > 0) {
            val animationListener = recyclerView.findViewHolderForAdapterPosition(ANIMATION_HOLDER)
            val userControlsListener =
                recyclerView.findViewHolderForAdapterPosition(USER_CONTROLS_HOLDER)
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

    fun onSaveFile(fileName: String, listener: DialogFragmentListeners) = viewModelScope.launch(Dispatchers.IO) {
        if (doesMusicFolderExists()) {
            listener.onSetProgressDone(10)
            startRecording.set(false)
            val fileMp3: File?
            val ffmpeg: Mp3Converter = Mp3ClassConverter()
            val fileLogic: FileLogic = FileManager(app)
            //20 %
            listener.onSetProgressDone(20)

            // get from the new shared prrefences the mp3 quality recordtype
            val infoCovertToMp3 = InfoCovertToMp3(
                app = app,
                fileName = parseFileStringName(fileName),
                recordType = getMp3Settings().recordQuality
            )
            //30 %
            listener.onSetProgressDone(30)
            try {
                listener.onSetProgressDone(40)
                listener.onSetProgressDone(50)
                fileMp3 = ffmpeg.convertToMp3(infoCovertToMp3)
            } catch (e: Exception) {
                listener.onConversionFailed()
                return@launch
            }
            listener.onSetProgressDone(60)
            val dateCreated = timeStamp?.toString() ?: Date().toString()

            //70 %
            listener.onSetProgressDone(70)
            val saveFileAudio = AudioFileData(
                id = Date().time,
                uri = null,
                name = "$fileName.mp3",
                duration = playAudio.getDurationPlayback(fileMp3.path),
                sizeFile = getFileSizeCurrentRecording(fileMp3),
                date = timeStamp ?: Date(),
                fileAudio = fileMp3
            )
            listener.onSetProgressDone(80)
            val wasSuccessSaveOnDB = fileLogic.saveFile(saveFileAudio)
            listener.onSetProgressDone(90)
            if (wasSuccessSaveOnDB) {
                loadWhenCreateOneRecording()
                deleteMp3FileFromInternalStorage()
                listener.onSetProgressDone(100)
                listener.onConversionSuccess()
                wasItemAdd.postValue(Pair(true, list))
            } else {
                //acknowledge the user that the file  was writing fine but could  not write in db. should like  on Music folder.
            }
        } else {
            //Tell the user that no chance to use the app
        }
    }

    fun onCancelSave() {
        Log.d(TAG, "onCancelSave")
        startRecording.set(false)
    }

    fun deleteRecording(record: RecordAudio, position: Int) = viewModelScope.launch(Dispatchers.IO) {
        val fileLogic: FileLogic = FileManager(app)
        record.id?.let {
            val deleteAudio = AudioFileData(
                id = record.id,
                uri = record.contentUri,
                sizeFile = record.size,
                duration = record.duration,
                name = record.name,
                date = Date()
            )
            if (fileLogic.deleteFile(deleteAudio)) {
                list.removeAt(position)
                moduleItem.postValue(list)
            }
        }
    }


    override fun saveSettings(recordSettings: RecordSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            val recordSettingsQuality = RecordType.values().indexOfFirst { it == recordSettings.recordQuality }
            protoBuffersRepo.updateValue(recordSettingsQuality)
        }
    }

    private fun queryInfoToLoadRecyclerView(): List<Records> {
        val listRecordings = mutableListOf<Records>()

        val fileLogic: FileLogic = FileManager(app)
        val listFromMediaStore = fileLogic.queryFiles()

        listFromMediaStore.map { audioFileData ->
            listRecordings.add(
                Records(
                    RecordAudio(
                        name = audioFileData.name,
                        time = audioFileData.date.toString(),
                        duration = audioFileData.duration,
                        size = audioFileData.sizeFile,
                        playbackFile = "${audioFileData.dataPlayback}",
                        contentUri = audioFileData.contentUri,
                        id = audioFileData.id
                    )
                )
            )
        }
        return listRecordings
    }

    private fun getFileSizeCurrentRecording(currentFileRecorded: File?): String {
        var size = "0 kbs"
        currentFileRecorded?.let {
            val bytes = it.length()
            val kilobytes = bytes / 1024
            val megabytes = kilobytes / 1024
            size = if (kilobytes.toInt() == 0 && megabytes.toInt() == 0) {
                "$bytes bytes"
            } else if (megabytes <= 0) {
                "$kilobytes kbs"
            } else {
                val kiloReminders = kilobytes % 1024
                if (kiloReminders > 0) {
                    "$megabytes.$kiloReminders mb"
                } else {
                    "$megabytes mb"
                }

            }
        }
        return size
    }

    private fun doesMusicFolderExists(): Boolean {
        val f = File(Environment.getExternalStorageDirectory(), FileManager.NEW_ALBUM_NAME)
        if (!f.exists()) {
            f.mkdirs()
        }
        return f.exists()
    }

    private fun parseFileStringName(fileName: String): String {
        var finalStringName = ""
        val fileNameWithOutPunctuations = removePunctuations(fileName)
        val arrayName = fileNameWithOutPunctuations.split(" ")
        if (arrayName.isNotEmpty()) {

            var i = 0
            while (i < arrayName.size) {
                if (i < arrayName.size - 1) {
                    finalStringName += "${arrayName[i]}_"
                }
                i++
            }
            finalStringName += arrayName[--i]
            if (finalStringName.contains(".mp3").not()) {
                finalStringName += Date().time.toString() + ".mp3"
            } else {
                val splitAgain = finalStringName.split(".mp3")
                finalStringName = ""
                finalStringName += splitAgain[0] + Date().time.toString() + ".mp3"
            }

        } else {
            finalStringName += if (fileName.contains(".mp3").not()) {
                Date().time.toString() + ".mp3"
            } else {
                val splitAgain = fileName.split("mp3")
                splitAgain[0] + Date().time.toString() + ".mp3"
            }
        }
        return finalStringName
    }

    private fun removePunctuations(source: String): String {
        return source.replace("[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]".toRegex(), "")
    }

    private fun deleteMp3FileFromInternalStorage() = viewModelScope.launch(Dispatchers.IO) {
        val downloadFolder = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        downloadFolder?.listFiles()?.iterator()?.forEachRemaining { file ->
            file?.let {
                if (it.name.contains(MP3)) {
                    it.delete()
                }
            }
        }
    }

    fun shareMp3File(record: RecordAudio) = viewModelScope.launch(Dispatchers.IO) {
        val folder = File(FileManager.PATH_TO_EXTERNAL_STORAGE)
        val files = folder.listFiles()
        val fileToSend = files?.firstOrNull { it.name.equals(record.name) }
        fileToSend?.let {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, record.contentUri)
                type = AUDIO_MIME_TYPE
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            shareLiveData.postValue(shareIntent)
        }
    }

    private suspend fun getMp3Settings(): RecordSettings {
        return when (protoBuffersRepo.readProto.first().filter) {
            RecordSettingsOption.Filter.MP3_SUPER_LOW -> RecordSettings(RecordType.MP3_SUPER_LOW)
            RecordSettingsOption.Filter.MP3_LOW -> RecordSettings(RecordType.MP3_LOW)
            RecordSettingsOption.Filter.MP3_MEDIUM -> RecordSettings(RecordType.MP3_MEDIUM)
            RecordSettingsOption.Filter.MP3_MEDIUM_HIGH -> RecordSettings(RecordType.MP3_MEDIUM_HIGH)
            RecordSettingsOption.Filter.MP3_HIGH -> RecordSettings(RecordType.MP3_HIGH)
            RecordSettingsOption.Filter.MP3_HIGHEST -> RecordSettings(RecordType.MP3_HIGHEST)
            RecordSettingsOption.Filter.UNRECOGNIZED -> RecordSettings(RecordType.MP3_MEDIUM)
            else -> RecordSettings(RecordType.MP3_MEDIUM_HIGH)
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecordViewModel(app) as T
        }
    }

    companion object {
        const val TAG = "RecordViewModel"
        private const val ANIMATION_HOLDER = 1
        private const val USER_CONTROLS_HOLDER = 2
        private const val HALF_SECOND = 1000L
        private const val ONE_HOUR_IN_SECS = 3600
        private const val MP3 = ".mp3"
        private const val AUDIO_MIME_TYPE = "audio/mpeg"
    }

}
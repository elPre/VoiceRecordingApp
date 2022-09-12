package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import android.app.Activity
import android.util.Log
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.UserInteractionInformationHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.UserControls
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class UserInteractionControlsHolder(
    private val view: UserInteractionInformationHolderBinding,
    private val action: (Events) -> Unit
) : BaseRecordViewHolder<UserControls>(view.root),
    ObtainHolderForActionEvent {

    private val activity = view.root.context as? Activity

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            action(Events.SeekBarReflectOnTimer(progress.div(100)))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) { }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            action(Events.SeekBarAudio((seekBar?.progress?.div(100)) ?: 0))
        }
    }

    init {

        view.stopBtn.isVisible = true
        view.playBtn.isVisible = true
        view.pauseBtn.isVisible = true
        view.seekBar.isVisible = false
        view.duration.isVisible = false

        view.playBtn.setOnClickListener {
            action(Events.Play)
        }

        view.recordBtn.setOnClickListener {
            view.playBtn.isEnabled = false
            action(Events.Record)
        }
        view.stopBtn.setOnClickListener {
            action(Events.Stop)
        }

        view.pauseBtn.setOnClickListener {
            action(Events.Pause)
        }

        view.seekBar.setOnSeekBarChangeListener(seekBarListener)
        view.seekBar.progress = 0
    }

    override fun bind(item: UserControls){ }

    override fun onClockTick(msg: String) {
        activity?.runOnUiThread {
            view.timeRecording.text = msg
        }
    }

    override fun setMaxSeekBar(maxString: String, maxInt: Int) {
        activity?.runOnUiThread {
            view.seekBar.max = maxInt * 100 // allows the user to have a great seek bar experience
            view.duration.text = maxString
        }
    }

    override fun updateSeekBar(updateSeekBar: Int) {
        view.seekBar.setProgress(updateSeekBar * 100, false)
    }

    override fun showHideSeekBar(show: Boolean) {
        activity?.runOnUiThread {
            view.seekBar.isVisible = show
            view.duration.isVisible = show
        }
    }

    override fun onFinishPlayback() {

    }

    override fun onRecording(isRecording: Boolean) {
        if (isRecording) {
            view.pauseBtn.setOnClickListener {
                action(Events.Pause)
            }
        } else {
            view.pauseBtn.setOnClickListener {
                view.playBtn.isEnabled = true
                action(Events.PausePlayback)
            }
        }
    }
}
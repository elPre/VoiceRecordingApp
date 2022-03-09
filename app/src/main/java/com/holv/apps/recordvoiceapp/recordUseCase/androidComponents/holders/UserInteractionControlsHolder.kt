package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.UserInteractionInformationHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.UserControls
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class UserInteractionControlsHolder(
    private val view: UserInteractionInformationHolderBinding,
    private val action: (Events) -> Unit
) : BaseRecordViewHolder<UserControls>(view.root),
    ObtainHolderForActionEvent {

    init {
        val resource = view.root.resources
        view.playBtn.setOnClickListener {
            val text = view.playBtn.text
            if (text.equals(resource.getString(R.string.play))) {
                view.playBtn.text = resource.getString(R.string.pause)
                action(Events.Play)
            } else {
                view.playBtn.text = resource.getString(R.string.play)
                action(Events.PausePlayback)
            }

        }
        view.recordBtn.setOnClickListener {
            val text = view.recordBtn.text
            if (text.equals(resource.getString(R.string.record))) {
                view.recordBtn.text = resource.getString(R.string.pause)
                action(Events.Record)
            } else {
                view.recordBtn.text = resource.getString(R.string.record)
                action(Events.Pause)
            }

        }
        view.stopBtn.setOnClickListener {
            view.playBtn.text = resource.getString(R.string.play)
            view.recordBtn.text = resource.getString(R.string.record)
            action(Events.Stop)
        }
    }

    override fun bind(item: UserControls) { }

    override fun onClockTick(msg: String) {
        view.timeRecording.text = msg
    }

    override fun setMaxSeekBar(maxString: String, maxInt: Int) {
        view.seekBar.max = maxInt * 100 // allows the user to have a great seek bar experience
        view.duration.text = maxString
    }

    override fun onFinishPlayback() {
        view.playBtn.text = view.root.resources.getString(R.string.play)
    }
}
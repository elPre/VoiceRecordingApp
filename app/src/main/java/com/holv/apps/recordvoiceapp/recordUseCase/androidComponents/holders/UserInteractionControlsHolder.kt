package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.holv.apps.recordvoiceapp.databinding.UserInteractionInformationHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.UserControls
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class UserInteractionControlsHolder(
    private val view: UserInteractionInformationHolderBinding,
    private val action: (Events) -> Unit
) : BaseRecordViewHolder<UserControls>(view.root) {

    init {
        view.playBtn.setOnClickListener {
            action(Events.Play)
        }
        view.recordBtn.setOnClickListener {
            action(Events.Record)
        }
        view.stopBtn.setOnClickListener {
            action(Events.Stop)
        }
    }

    override fun bind(item: UserControls) {}
}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.holv.apps.recordvoiceapp.databinding.LegendRecordingsHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.LegendRecordings
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class LegendRecordingsHolder(
    private val view: LegendRecordingsHolderBinding,
    private val action: (Events) -> Unit
): BaseRecordViewHolder<LegendRecordings>(view.root) {

    init {
        view.ivSettings.setOnClickListener {
            action(Events.OpenSettings)
        }
    }

    override fun bind(item: LegendRecordings) { }
}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import android.app.Activity
import androidx.core.view.isVisible
import com.holv.apps.recordvoiceapp.databinding.LegendRecordingsHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.LegendRecordings
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class LegendRecordingsHolder(
    private val view: LegendRecordingsHolderBinding,
    private val action: (Events) -> Unit
): BaseRecordViewHolder<LegendRecordings>(view.root), ShowLegendOnRecordings {

    private val activity = view.root.context as? Activity

    init {
        view.ivSettings.setOnClickListener {
            action(Events.OpenSettings)
        }
    }
    override fun bind(item: LegendRecordings) { }

    override fun setupLegend(legend: String) {
        activity?.runOnUiThread {
            view.tvPlaybackRecordName.text = legend
            view.tvPlaybackRecordName.isVisible = true
        }
    }

}
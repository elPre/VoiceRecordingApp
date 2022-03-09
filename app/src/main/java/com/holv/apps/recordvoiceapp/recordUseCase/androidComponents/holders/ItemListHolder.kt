package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import android.view.View
import android.widget.Toast
import com.holv.apps.recordvoiceapp.databinding.ItemRecordingBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.RecordItem
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Records
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class ItemListHolder(
    private val view: ItemRecordingBinding,
    private val action: (Events) -> Unit,
    private val getItem: (Int) -> RecordItem
): BaseRecordViewHolder<Records>(view.root) {

    init {
        view.root.setOnClickListener {
            getItem(adapterPosition).let {
                val record = it as Records
                Toast.makeText(view.root.context, "good Text ${record.recordAudio.name}", Toast.LENGTH_LONG).show()
                action(Events.PlayRecordedAudio(record.recordAudio))
            }
        }
    }

    override fun bind(item: Records) = with(view) {
        with(item) {
            recordingName.text = recordAudio.name
            duration.text = recordAudio.duration
            fileSize.text = recordAudio.time
        }
    }

}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.holv.apps.recordvoiceapp.databinding.ItemRecordingBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Records
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class ItemListHolder(
    private val view: ItemRecordingBinding,
    private val action: (Events) -> Unit
): BaseRecordViewHolder<Records>(view.root) {
    override fun bind(item: Records) { }
}
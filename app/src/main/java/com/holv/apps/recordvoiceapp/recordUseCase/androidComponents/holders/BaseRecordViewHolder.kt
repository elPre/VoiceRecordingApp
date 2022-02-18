package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.RecordItem

abstract class BaseRecordViewHolder <T : RecordItem>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
}
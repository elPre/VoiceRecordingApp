package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.holv.apps.recordvoiceapp.databinding.*
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders.*
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.inflater

class Adapter(
    private val action: (Events) -> Unit
) : ListAdapter<RecordItem, BaseRecordViewHolder<RecordItem>>(RecordingItemDiff) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (RecordType.values()[viewType]) {
        RecordType.TOP_BANNER -> TopBannerHolder(
            TopBannerHolderBinding.inflate(
                parent.inflater,
                parent,
                false
            ), action
        )
        RecordType.LOGO_ANIMATION -> AnimationHolder(
            AnimationHolderBinding.inflate(
                parent.inflater,
                parent,
                false
            ), action
        )
        RecordType.USER_CONTROLS -> UserInteractionControlsHolder(
            UserInteractionInformationHolderBinding.inflate(
                parent.inflater,
                parent,
                false
            ), action
        )
        RecordType.LEGEND_RECORDINGS -> LegendRecordingsHolder(
            LegendRecordingsHolderBinding.inflate(
                parent.inflater,
                parent,
                false
            ), action
        )
        RecordType.RECORDINGS_AUDIOS -> ItemListHolder(
            ItemRecordingBinding.inflate(
                parent.inflater,
                parent,
                false
            ), action, ::getItem
        )
    } as BaseRecordViewHolder<RecordItem>

    override fun onBindViewHolder(holder: BaseRecordViewHolder<RecordItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = getItem(position).itemType.ordinal

    fun getItemAt(position: Int) = currentList.getOrNull(position)

}


object RecordingItemDiff : DiffUtil.ItemCallback<RecordItem>() {
    override fun areItemsTheSame(oldItem: RecordItem, newItem: RecordItem) =
        oldItem.itemType == newItem.itemType
    override fun areContentsTheSame(oldItem: RecordItem, newItem: RecordItem) =
        oldItem == newItem
}
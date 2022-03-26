package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import com.holv.apps.recordvoiceapp.R
import com.holv.apps.recordvoiceapp.databinding.ItemRecordingBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.RecordItem
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.Records
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class ItemListHolder(
    private val binding: ItemRecordingBinding,
    private val action: (Events) -> Unit,
    private val getItem: (Int) -> RecordItem
): BaseRecordViewHolder<Records>(binding.root) {

    init {
        binding.root.setOnClickListener {
            getItem(adapterPosition).let {
                val record = it as Records
                action(Events.PlayRecordedAudio(record.recordAudio))
            }
        }
    }

    override fun bind(item: Records) = with(binding) {
        with(item) {
            recordingName.text = recordAudio.name
            duration.text = recordAudio.duration
            fileSize.text = recordAudio.time
            ibMenu.setOnClickListener {
                showOptionsPopup(view = it, recordItem = this)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showOptionsPopup(view: View, recordItem: Records) = PopupMenu(
        ContextThemeWrapper(view.context, R.style.popupMenu),
        binding.popupAnchor,
        GravityCompat.START,
        0,
        R.style.popupMenu
    ).apply {
        menuInflater.inflate(R.menu.audio_record_menu, menu)
        setOnMenuItemClickListener {
            val actionToAudio = recordItem.recordAudio
            when (it.itemId) {
                R.id.share_recording -> action(Events.ShareRecordedAudio(actionToAudio))
                R.id.delete_recording -> action(Events.DeleteRecordedAudio(actionToAudio, adapterPosition))
            }
            true
        }
    }.run {
        MenuPopupHelper(view.context, menu as MenuBuilder, binding.popupAnchor).apply {
            setForceShowIcon(true)
            show()
        }
    }

}
package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import androidx.core.view.isVisible
import com.holv.apps.recordvoiceapp.databinding.AnimationHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.LogoAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class AnimationHolder(
    private val view: AnimationHolderBinding,
    private val action: (Events) -> Unit
): BaseRecordViewHolder<LogoAnimation>(view.root), FireAnimation {

    override fun bind(item: LogoAnimation) { }

    override fun onFireAnimation(isTurn: Boolean) {
        when (isTurn) {
            true -> {
                view.imGifImage.isVisible = true
                view.lopperCustomView.isVisible = false
            }
            false -> {
                view.imGifImage.isVisible = false
                view.lopperCustomView.isVisible = true
            }
        }
    }
}
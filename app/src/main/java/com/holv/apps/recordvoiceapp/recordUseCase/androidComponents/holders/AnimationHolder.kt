package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.holv.apps.recordvoiceapp.databinding.AnimationHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.LogoAnimation
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class AnimationHolder(
    private val view: AnimationHolderBinding,
    private val action: (Events) -> Unit
): BaseRecordViewHolder<LogoAnimation>(view.root) {
    override fun bind(item: LogoAnimation) { }
}
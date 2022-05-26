package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.holders

import com.google.android.gms.ads.AdRequest
import com.holv.apps.recordvoiceapp.databinding.TopBannerHolderBinding
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.TopBanner
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.actionEvents.Events

class TopBannerHolder(
    private val view: TopBannerHolderBinding,
    private val action: (Events) -> Unit
) : BaseRecordViewHolder<TopBanner>(view.root) {

    override fun bind(item: TopBanner) {
        // Create an ad request.
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        view.adViewOne.loadAd(adRequest)
    }

}
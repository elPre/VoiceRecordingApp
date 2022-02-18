package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters

import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.pojos.RecordAudio

sealed class RecordItem(val itemType: RecordType)

object TopBanner : RecordItem(RecordType.TOP_BANNER)
object LogoAnimation : RecordItem(RecordType.LOGO_ANIMATION)
object UserControls : RecordItem(RecordType.USER_CONTROLS)
object LegendRecordings : RecordItem(RecordType.LEGEND_RECORDINGS)
data class Records(val recordAudio: RecordAudio) : RecordItem(RecordType.RECORDINGS_AUDIOS)

enum class RecordType {
    TOP_BANNER,
    LOGO_ANIMATION,
    USER_CONTROLS,
    LEGEND_RECORDINGS,
    RECORDINGS_AUDIOS
}

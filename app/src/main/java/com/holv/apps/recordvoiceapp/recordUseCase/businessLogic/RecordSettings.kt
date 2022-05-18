package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

data class RecordSettings(
    val recordQuality: RecordType
)

enum class RecordType(val biteRate:  Int, val sampleRate: Int, val mp3Quality: Int) {
    MP3_SUPER_LOW (64000, 44100, 9),
    MP3_LOW (96000, 44100, 8),
    MP3_MEDIUM (128000, 44100, 6),
    MP3_MEDIUM_HIGH (192000, 44100, 4),
    MP3_HIGH (256000, 44100, 2),
    MP3_HIGHEST (320000, 44100, 0),
}
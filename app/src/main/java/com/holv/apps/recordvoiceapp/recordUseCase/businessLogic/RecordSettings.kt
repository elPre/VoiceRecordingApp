package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic

data class RecordSettings(
    val recordQuality: RecordType
)

enum class RecordType(val biteRate:  Int, val sampleRate: Int, val mp3Quality: Int) {
    MP3_SUPER_LOW (64, 44100, 9),
    MP3_LOW (96, 44100, 8),
    MP3_MEDIUM (128, 44100, 6),
    MP3_MEDIUM_HIGH (192, 44100, 4),
    MP3_HIGH (256, 44100, 2),
    MP3_HIGHEST (320, 44100, 0),
}
package com.holv.apps.recordvoiceapp.recordUseCase.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserRecordSettingsSerializer : Serializer<RecordSettingsOption> {

    override val defaultValue: RecordSettingsOption
        get() = RecordSettingsOption.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): RecordSettingsOption {
        try {
            return RecordSettingsOption.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    }

    override suspend fun writeTo(t: RecordSettingsOption, output: OutputStream) = t.writeTo(output)

}
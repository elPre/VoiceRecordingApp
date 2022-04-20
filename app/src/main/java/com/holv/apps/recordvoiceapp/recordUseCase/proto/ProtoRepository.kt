package com.holv.apps.recordvoiceapp.recordUseCase.proto

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.util.userPreferencesStore
import com.holv.apps.recordvoiceapp.recordUseCase.businessLogic.RecordSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class ProtoRepository(context: Context) {

    private val dataStore: DataStore<RecordSettingsOption> = context.userPreferencesStore

    val readProto: Flow<RecordSettingsOption> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(RecordSettingsOption.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateValue(recordSettings: Int) {
        dataStore.updateData { mp3Settings ->
            mp3Settings.toBuilder().setFilterValue(recordSettings).build()
        }
    }

}



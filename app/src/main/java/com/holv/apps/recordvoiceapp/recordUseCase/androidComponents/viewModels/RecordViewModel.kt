package com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holv.apps.recordvoiceapp.recordUseCase.androidComponents.adapters.*
import kotlinx.coroutines.launch

class RecordViewModel(val app: Application) : ViewModel() {

    val moduleItem = MutableLiveData<List<RecordItem>>()

    fun loadPage() = viewModelScope.launch {
        load()
    }

    private fun load() {
        val list =  mutableListOf<RecordItem>()
        list.add(TopBanner)
        list.add(LogoAnimation)
        list.add(UserControls)
        list.add(LegendRecordings)
        //load all the recordings
        moduleItem.postValue(list.toList())
    }


    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RecordViewModel(app) as T
        }
    }
}
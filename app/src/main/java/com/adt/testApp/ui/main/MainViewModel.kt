package com.adt.testApp.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adt.testApp.ui.main.rest.ADTApi
import com.adt.testApp.ui.main.rest.models.CharacterActor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    enum class DataState {
        NOT_LOADING,
        LOADING,
        LOADED,
        LOADING_ERROR
    }
    private var totalCharacterActors: Int = 1
    private var next: String? = null
    private var nextPage: Int = 0
    private var bEndOfList = false

    // The login status - true when user is logged in, false when not logged in.
    private val _loadingStatus = MutableLiveData<DataState>()
    val loadingStatus: LiveData<DataState>
        get() = _loadingStatus


    private var cachedCharacterActorArrayList : ArrayList<CharacterActor> = ArrayList()
    private val _characterActorList = MutableLiveData<List<CharacterActor>>()
    val vendorCharacterActorList: LiveData<List<CharacterActor>>
        get() = _characterActorList


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private fun loadCharacterActors(offset:Int){
        _loadingStatus.value = DataState.LOADING
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val characterListDeferred = ADTApi.retrofitService.getcharacterAsync(nextPage)
            try {
                // this will run on a thread managed by Retrofit
                val result = characterListDeferred.await()
                totalCharacterActors = result.results.count()
                next = result.info.next
                if( next== null){
                    bEndOfList = true
                } else {
                    val uri: Uri = Uri.parse(next)
                    val pageString = uri.getQueryParameter("page")
                    if (!pageString.isNullOrEmpty()) {
                        nextPage = pageString.toInt()
                    }

                }
                if(offset==0){
                    cachedCharacterActorArrayList.clear()
                }
                cachedCharacterActorArrayList.addAll(result.results.toList())
                _characterActorList.value = cachedCharacterActorArrayList.toList()
                _loadingStatus.value = DataState.LOADED
            } catch (e: Exception) {
                _loadingStatus.value = DataState.LOADING_ERROR
            }
        }
    }


    fun loadPage(lastVisibleItemPosition: Int) {
        if(lastVisibleItemPosition >= cachedCharacterActorArrayList.size && !bEndOfList){
            if(_loadingStatus.value != DataState.LOADING) {
                loadCharacterActors(cachedCharacterActorArrayList.size)
            }
        }
    }

    fun resume() {
        if(cachedCharacterActorArrayList.isEmpty() ) {
            loadCharacterActors(0)
        } else {
            _characterActorList.value = cachedCharacterActorArrayList.toList()
        }
    }

}
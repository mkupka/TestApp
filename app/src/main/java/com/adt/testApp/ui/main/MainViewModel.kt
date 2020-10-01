package com.adt.testApp.ui.main

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
    private var lastVisibleItemPosition: Int = 0
    private var firstVisibleItemPosition: Int = 0

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

    private fun loadCharacterActors(limit:Int, offset:Int){
        _loadingStatus.value = DataState.LOADING
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val characterListDeferred = ADTApi.retrofitService.getcharacterAsync()
            try {
                // this will run on a thread managed by Retrofit
                val result = characterListDeferred.await()
                totalCharacterActors = result.results.count()
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


    fun setRecyclerPosition(firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        this.firstVisibleItemPosition = firstVisibleItemPosition
        this.lastVisibleItemPosition = lastVisibleItemPosition
        if(lastVisibleItemPosition >= cachedCharacterActorArrayList.size && lastVisibleItemPosition < totalCharacterActors){
            if(_loadingStatus.value != DataState.LOADING) {
                loadCharacterActors(50, cachedCharacterActorArrayList.size)
            }
        }
    }

    fun resume() {
        if(cachedCharacterActorArrayList.isEmpty() ) {
            loadCharacterActors(50,0)
        } else {
            _characterActorList.value = cachedCharacterActorArrayList.toList()
        }
    }

}
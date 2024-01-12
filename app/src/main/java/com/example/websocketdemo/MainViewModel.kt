package com.example.websocketdemo

import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {


    private val _socketStatus= MutableStateFlow<Boolean>(false)
    val socketStatus=_socketStatus.asStateFlow()

    private val _message= MutableStateFlow<Pair<Boolean,String>>(Pair(false,""))
    val message=_message.asStateFlow()


    fun setStatus(status:Boolean){
        viewModelScope.launch {
            _socketStatus.value=status
        }
    }

    fun setMessage(message: Pair<Boolean,String>){
        viewModelScope.launch(Dispatchers.Main) {
          if(_socketStatus.value){
              _message.value=message
          }
        }
    }



}
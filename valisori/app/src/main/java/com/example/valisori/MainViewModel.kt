package com.example.valisori

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val inputText = MutableLiveData("")
    val onVoiceButtonClicked = MutableLiveData(false)
    val onInputButtonClicked = MutableLiveData(false)
    fun onClickInputButton() {
        onInputButtonClicked.value = true
    }

    fun onClickVoiceButton() {
        onVoiceButtonClicked.value = true
    }
}
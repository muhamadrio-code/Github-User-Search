package com.muhammadrio.githubuser.ui.dialogs

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ThemeSelectionDialogViewModel : ViewModel() {

    private val _mode = MutableLiveData(0)
    val mode: LiveData<Int> = _mode

    private var currentMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private var currentPosition = 0

    fun getCurrentPosition() : Int {
        return when(currentMode){
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            AppCompatDelegate.MODE_NIGHT_NO-> 1
            else -> 0
        }
    }

    fun onSelectTheme(){
        _mode.value = when(currentPosition){
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun setCurrentMode(mode: Int) {
        currentMode = mode
    }

    fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

}
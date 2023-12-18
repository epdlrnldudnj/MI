package com.example.mi.ui.Day

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class DayViewModel : ViewModel() {
    private val _currentDate = MutableLiveData<LocalDate>()
    init {
        getCurrentDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate() {
        _currentDate.value = LocalDate.now()
    }
}
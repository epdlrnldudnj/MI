package com.example.mi.ui.Calender
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CalendarViewModel : ViewModel() {

    private val _daysLiveData = MutableLiveData<List<Day>>()
    val daysLiveData: LiveData<List<Day>> = _daysLiveData

    private val _currentMonth = MutableLiveData<Int>()
    val currentMonth: LiveData<Int> = _currentMonth

    init {
        loadDaysForMonth()
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH)
    }
    private fun loadDaysForMonth() {
        val days = mutableListOf<Day>()
        val calendar = Calendar.getInstance()

        // Set to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (days.size < 42) { // Total cells of a 6-row calendar grid
            days.add(Day(calendar.time, calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        _daysLiveData.value = days
        _currentMonth.value = calendar.get(Calendar.MONTH)//현재 달 저장 합수
    }

    data class Day(val date: Date, val isCurrentMonth: Boolean)
}

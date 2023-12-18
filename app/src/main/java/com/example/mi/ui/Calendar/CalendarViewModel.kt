package com.example.mi.ui.Calendar
// CalendarViewModel.kt
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSelectedDate(): LocalDate = selectedDate

    @RequiresApi(Build.VERSION_CODES.O)
    fun goToPreviousMonth() {
        selectedDate = selectedDate.minusMonths(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun goToNextMonth() {
        selectedDate = selectedDate.plusMonths(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return selectedDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysInMonthArray(): ArrayList<LocalDate?> {
        val daysInMonthArray = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(selectedDate)

        // 달의 첫날
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        // 첫날의 요일 (1: Monday, ... 7: Sunday)
        var dayOfWeek = firstOfMonth.dayOfWeek.value

        // 일요일을 한 주의 시작으로 설정 (일: 0, 월: 1, ..., 토: 6)
        dayOfWeek -= 1
        if (dayOfWeek < 0) dayOfWeek = 6

        // 달력 시작 부분에 빈 칸 채우기
        for (i in 0 until dayOfWeek) {
            daysInMonthArray.add(null)
        }

        // 현재 달의 날짜 채우기
        val daysInMonth = yearMonth.lengthOfMonth()
        for (i in 1..daysInMonth) {
            daysInMonthArray.add(LocalDate.of(selectedDate.year, selectedDate.month, i))
        }

        // 달력 마지막 부분 빈 칸 채우기 (달력을 6주로 가정)
        while (daysInMonthArray.size % 7 != 0) {
            daysInMonthArray.add(null)
        }

        return daysInMonthArray
    }
}

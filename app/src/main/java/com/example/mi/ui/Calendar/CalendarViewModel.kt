package com.example.mi.ui.Calendar
// CalendarViewModel.kt
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {
    // 1.년월 날짜 가져오기위한 변수
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate = LocalDate.now()

    // 2. 해당월에서 -1
    @RequiresApi(Build.VERSION_CODES.O)
    fun goToPreviousMonth() {
        selectedDate = selectedDate.minusMonths(1)
    }

    // 3. 해당월에서 +1
    @RequiresApi(Build.VERSION_CODES.O)
    fun goToNextMonth() {
        selectedDate = selectedDate.plusMonths(1)
    }

    // 4. 날짜 포맷하는 메서드
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return this.selectedDate.format(formatter)
    }

    // 데이터를 노출하는 함수들 (LiveData 또는 StateFlow를 사용할 수 있음)
    fun getSelectedDate(): LocalDate {
        return selectedDate
    }
}

package com.example.mi.ui.Calendar
// DaysAdapter.kt
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ItemDayBinding
import java.time.LocalDate

class DaysAdapter(private val days: ArrayList<LocalDate>) :
    RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(day: LocalDate) {
            binding.textViewDayNumber.text = day.dayOfMonth.toString()
            // 여기서 추가적인 날짜 정보를 textViewMonth, textViewDay에 바인딩할 수 있습니다.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayBinding.inflate(inflater, parent, false)
        return DayViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return days.size
    }
}

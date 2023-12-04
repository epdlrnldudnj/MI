package com.example.mi.ui.Calender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ItemDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DaysAdapter : ListAdapter<CalendarViewModel.Day, DaysAdapter.DayViewHolder>(DIFF_CALLBACK) {

    private var currentMonth: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {//현재 달을 확인하고 이를 저장
        val day = getItem(position)
        if (holder.adapterPosition == 0) { // 첫 아이템일 경우 현재 달을 업데이트
            currentMonth = Calendar.getInstance().apply {
                time = day.date
            }.get(Calendar.MONTH)
        }
        holder.bind(day)
    }

    class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: CalendarViewModel.Day) {
            binding.textViewDayNumber.text = SimpleDateFormat("d", Locale.getDefault()).format(day.date)
            // Highlight the cell if it is in the current month
            binding.textViewDayNumber.alpha = if (day.isCurrentMonth) 1.0f else 0.5f
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CalendarViewModel.Day>() {
            override fun areItemsTheSame(oldItem: CalendarViewModel.Day, newItem: CalendarViewModel.Day): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: CalendarViewModel.Day, newItem: CalendarViewModel.Day): Boolean {
                return oldItem.isCurrentMonth == newItem.isCurrentMonth
            }
        }
    }
}

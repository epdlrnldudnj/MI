package com.example.mi.ui.Day

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ItemDayBinding
import com.example.mi.ui.Calender.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DaysAdapter : ListAdapter<CalendarViewModel.Day, DaysAdapter.DayViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: CalendarViewModel.Day) {
            binding.textViewDay.text = SimpleDateFormat("d", Locale.getDefault()).format(day.date)
            // Highlight the cell if it is in the current month
            binding.textViewDay.alpha = if (day.isCurrentMonth) 1.0f else 0.5f
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

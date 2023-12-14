package com.example.mi.ui.Calendar
// DaysAdapter.kt
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ItemDayBinding
import java.time.LocalDate

class DaysAdapter(var days: ArrayList<LocalDate?>) :
    RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    class DayViewHolder(private val binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(day: LocalDate?) {
            if (day != null) {
                binding.textViewDayNumber.text = day.dayOfMonth.toString()
                // Set any additional text or styles here
            } else {
                // Handle the cell if it's meant to be empty
                binding.textViewDayNumber.text = ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

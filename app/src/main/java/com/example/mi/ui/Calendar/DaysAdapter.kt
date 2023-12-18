package com.example.mi.ui.Calendar
// DaysAdapter.kt
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ItemDayBinding
import java.time.LocalDate

// DaysAdapter.kt
class DaysAdapter(
    var days: ArrayList<LocalDate?>,
    private val onDayClickListener: OnDayClickListener // 리스너를 전달받는 생성자 파라미터 추가
) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    interface OnDayClickListener {
        fun onDayClick(date: LocalDate?)
    }

    class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(day: LocalDate?, clickListener: OnDayClickListener) { // 클릭 리스너 파라미터 추가
            if (day != null) {
                binding.textViewDayNumber.text = day.dayOfMonth.toString()
                binding.root.setOnClickListener { // 아이템 뷰 클릭 리스너 설정
                    clickListener.onDayClick(day)
                }
            } else {
                binding.textViewDayNumber.text = ""
                binding.root.setOnClickListener(null)
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
        holder.bind(day, onDayClickListener) // 클릭 리스너를 bind 함수에 전달
    }

    override fun getItemCount(): Int {
        return days.size
    }
}

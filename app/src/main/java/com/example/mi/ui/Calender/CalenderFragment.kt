package com.example.mi.ui.Calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mi.databinding.FragmentCalenderBinding
import java.text.DateFormatSymbols

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysAdapter = DaysAdapter()
        binding.recyclerViewCalendar.apply {
            layoutManager = GridLayoutManager(context, 7) // 7 for a week's days
            adapter = daysAdapter
        }

        viewModel.daysLiveData.observe(viewLifecycleOwner) { days ->
            daysAdapter.submitList(days)

        }
        viewModel.currentMonth.observe(viewLifecycleOwner) { month -> //현재 달을 표시하는 UI
            val monthString = DateFormatSymbols().months[month]
        }
    }
}

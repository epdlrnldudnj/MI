package com.example.mi.ui.Calendar
// CalendarFragment.kt
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mi.databinding.FragmentCalendarBinding


class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        val daysAdapter = DaysAdapter(viewModel.getDaysInMonthArray())
        binding.recyclerViewCalendar.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = daysAdapter
        }

        updateMonthDisplay()

        binding.preBtn.setOnClickListener {
            viewModel.goToPreviousMonth()
            updateCalendar(daysAdapter)
        }

        binding.nextBtn.setOnClickListener {
            viewModel.goToNextMonth()
            updateCalendar(daysAdapter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendar(daysAdapter: DaysAdapter) {
        daysAdapter.days = viewModel.getDaysInMonthArray()
        daysAdapter.notifyDataSetChanged()
        updateMonthDisplay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthDisplay() {
        binding.textViewMonthYear.text = viewModel.getFormattedDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

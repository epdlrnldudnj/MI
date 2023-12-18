package com.example.mi.ui.Calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mi.R
import com.example.mi.databinding.FragmentCalendarBinding
import com.example.mi.databinding.FragmentDayBinding
import java.time.LocalDate

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
        //binding = FragmentDayBinding.bind(view)

        // Bundle에서 선택된 날짜 가져오기
        val selectedDate = arguments?.getString("selectedDate")
        // 선택된 날짜를 사용하여 화면 업데이트 또는 다른 처리
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        val daysAdapter = DaysAdapter(viewModel.getDaysInMonthArray(), object : DaysAdapter.OnDayClickListener {
            override fun onDayClick(date: LocalDate?) {
                date?.let {
                    // 날짜 선택 시 동작
                    val bundle = Bundle()
                    bundle.putString("selectedDate", it.toString()) // 선택된 날짜를 문자열로 변환하여 Bundle에 추가

                    findNavController().navigate(R.id.navigation_day, bundle) // DayFragment로 이동
                }
            }
        })
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

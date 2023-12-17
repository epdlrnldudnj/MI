package com.example.mi.ui.Day

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mi.R
import com.example.mi.databinding.FragmentDayBinding
import java.time.format.DateTimeFormatter

class DayFragment : Fragment(R.layout.fragment_day) {
    private val dayViewModel: DayViewModel by viewModels()
    private var binding: FragmentDayBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDayBinding.bind(view)

        dayViewModel.currentDate.observe(viewLifecycleOwner, { date ->
            binding?.dayDate?.text = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // View Binding 정리
    }
}

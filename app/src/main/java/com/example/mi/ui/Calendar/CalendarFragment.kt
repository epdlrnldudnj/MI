package com.example.mi.ui.Calendar
// CalendarFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이전 달 버튼 클릭 리스너
        binding.preBtn.setOnClickListener {
            viewModel.goToPreviousMonth()
            updateUI()
        }

        // 다음 달 버튼 클릭 리스너
        binding.nextBtn.setOnClickListener {
            viewModel.goToNextMonth()
            updateUI()
        }

        // UI 초기 업데이트
        updateUI()
    }

    // 5. 화면에 날짜 보여주는 메서드
    private fun updateUI() {
        binding.textViewMonthYear.text = viewModel.getFormattedDate()
        // RecyclerView 갱신 로직도 여기에 포함시킬 수 있습니다.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

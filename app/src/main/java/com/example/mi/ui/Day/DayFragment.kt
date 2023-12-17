package com.example.mi.ui.Day

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
        binding?.btnMoodBox?.setOnClickListener {
            showMoodSelector()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // View Binding 정리
    }
        private fun showMoodSelector() {
            val moods = arrayOf("행복","기쁨", "슬픔", "화남", "평온", "피곤", "체크") // 이모지나 텍스트를 배열로 정의합니다.
            AlertDialog.Builder(requireContext())
                .setTitle("기분을 선택하세요")
                .setItems(moods) { dialog, which ->
                    // 사용자가 선택한 이모지를 박스 안에 표시하는 로직
                    val selectedMood = moods[which]
                    binding?.btnMoodBox?.setImageResource(getImageResourceForMood(selectedMood))
                }
                .show()
        }

        private fun getImageResourceForMood(mood: String): Int {
            // mood에 따라 다른 이미지 리소스 ID를 반환합니다.
            return when (mood) {
                "화남" -> R.drawable.angry
                "기쁨" -> R.drawable.joy
                "행복" -> R.drawable.love
                "평온" -> R.drawable.clamdown
                "슬픔" -> R.drawable.depression
                "피곤" -> R.drawable.tiredness
                "체크" -> R.drawable.check
                // 여기에 다른 기분에 따른 이미지 리소스를 추가합니다.
                else -> R.drawable.check
            }
        }
}

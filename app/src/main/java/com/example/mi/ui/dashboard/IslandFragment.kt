package com.example.mi.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mi.databinding.FragmentIslandBinding

class IslandFragment : Fragment() {

    private var _binding: FragmentIslandBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?



    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(IslandViewModel::class.java)

        _binding = FragmentIslandBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 버튼과 텍스트뷰 찾기
        val testButton: Button = binding.testbutton
        val textView: TextView = binding.textView2

        // 버튼 클릭 리스너 등록
        testButton.setOnClickListener {
            // 텍스트뷰의 내용 변경

            textView.text = "버튼이 눌렸습니다"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
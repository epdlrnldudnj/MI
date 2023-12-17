package com.example.mi.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mi.MindPiece
import com.example.mi.databinding.FragmentIslandBinding
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.mi.ShoppingPage
import com.example.mi.ui.dashboard.IslandViewModel

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
        val pieceText: TextView = binding.piece

        val myApp = requireActivity().application as MindPiece
        var piece = myApp.mindpiece
        pieceText.text = piece.toString()

        testButton.setOnClickListener {
            // 텍스트뷰의 내용 변경
            myApp.addpiece(50)
            piece = myApp.mindpiece
            pieceText.text = piece.toString()
        }

        val shoppingButton: ImageButton = binding.shoppingbutton

        shoppingButton.setOnClickListener{
            val intent = Intent(requireContext(), ShoppingPage::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
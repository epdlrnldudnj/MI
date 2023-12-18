package com.example.mi.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mi.LoginActivity
import com.example.mi.databinding.FragmentIslandBinding
import com.example.mi.ShoppingPage
import com.example.mi.StoragePage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IslandFragment : Fragment() {

    private var _binding: FragmentIslandBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(IslandViewModel::class.java)

        _binding = FragmentIslandBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Firebase Authentication 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // 버튼과 텍스트뷰 찾기
        val testButton: Button = binding.testbutton
        val pieceText: TextView = binding.piece

        testButton.setOnClickListener {
            updateMindPiece(10)
        }

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        loadData()

        val shoppingButton: ImageButton = binding.shoppingbutton

        shoppingButton.setOnClickListener {
            val intent = Intent(requireContext(), ShoppingPage::class.java)
            startActivity(intent)
        }

        val storageButton: ImageButton = binding.storageButton
        storageButton.setOnClickListener{
            val intent = Intent(requireContext(), StoragePage::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 처리
        val logoutButton: ImageButton = binding.btnLogout

        logoutButton.setOnClickListener {
            // Firebase Authentication을 이용한 로그아웃
            auth.signOut()

            // 로그아웃 성공 메시지 출력
            Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()

            // 로그인 화면으로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadData() {
        val uid = auth.currentUser?.uid
        uid?.let {
            // Firestore에서 데이터를 불러오기
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Document가 존재할 때 데이터를 TextView에 표시
                        val mindPieceValue = document.getLong("MindPiece")
                        binding.piece.text = mindPieceValue?.toString() ?: "0"
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
    private fun updateMindPiece(how: Int) {
        val uid = auth.currentUser?.uid
        uid?.let {
            // Firestore에서 현재 MindPiece 값을 불러오기
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // 현재 MindPiece 값을 가져오기
                        val currentMindPiece = document.getLong("MindPiece") ?: 0

                        // 증가 혹은 감소된 값을 계산
                        val updatedMindPiece = currentMindPiece + how

                        // Firestore에 업데이트된 MindPiece 값을 저장
                        db.collection("users").document(uid)
                            .update("MindPiece", updatedMindPiece)
                            .addOnSuccessListener {
                                // 업데이트 성공 시 TextView에 표시
                                binding.piece.text = updatedMindPiece.toString()
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error updating document", exception)
                            }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
}

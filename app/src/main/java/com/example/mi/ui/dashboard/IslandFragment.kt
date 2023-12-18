package com.example.mi.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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

    data class Flower(val imageId: Int, val documentId: String)

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



        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        loadData()
        loadFlowers()

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
    private fun addFlowerImage(flower: Flower, x: Number, y:Number) {
        val imageView = ImageView(requireContext())
        val width = 200
        val height = 300
        imageView.setImageResource(flower.imageId)
        imageView.layoutParams = ViewGroup.LayoutParams(width, height)

        // 이미지뷰에 드래그 가능한 기능 추가
        imageView.setOnTouchListener { view, motionEvent ->
            handleDrag(view, motionEvent, flower)
        }

        // Firebase에서 가져온 x, y 좌표 설정
        val initialX = x.toFloat() // 이 값은 Firebase에서 가져와야 합니다.
        val initialY = y.toFloat() // 이 값은 Firebase에서 가져와야 합니다.
        imageView.x = initialX
        imageView.y = initialY

        // 이미지뷰를 IslandFragment에 추가
        binding.constraintLayout.addView(imageView)
    }


    private fun handleDrag(view: View, motionEvent: MotionEvent, flower: Flower): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_MOVE -> {
                // 드래그 중일 때 좌표값 변경
                view.x = motionEvent.rawX - view.width / 2
                view.y = motionEvent.rawY - view.height / 2

                // 변경된 좌표값을 Firebase에 업데이트
                updateFlowerCoordinates(flower, view.x, view.y)
            }
        }
        return true
    }

    private fun updateFlowerCoordinates(flower: Flower, x: Float, y: Float) {
        val uid = auth.currentUser?.uid
        uid?.let {
            // 해당 꽃의 좌표값을 Firebase에 업데이트
            db.collection("users").document(uid)
                .collection("FlowerList").document(flower.documentId)
                .update("x", x, "y", y)
                .addOnSuccessListener {
                    Log.d(TAG, "Flower coordinates updated successfully!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating flower coordinates", e)
                }
        }
    }

    private fun loadFlowers() {
        val uid = auth.currentUser?.uid
        uid?.let {
            // Firestore에서 status가 2인 꽃 데이터를 가져오기
            db.collection("users").document(uid)
                .collection("FlowerList")
                .whereEqualTo("status", 2)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val imageName = document.getString("name") ?: ""
                        val imageId = resources.getIdentifier(imageName, "drawable", requireContext().packageName)
                        val flower = Flower(imageId, document.id)
                        val x = document.getDouble("x") ?:0
                        val y = document.getDouble("y") ?:0
                        addFlowerImage(flower,x,y)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }
}

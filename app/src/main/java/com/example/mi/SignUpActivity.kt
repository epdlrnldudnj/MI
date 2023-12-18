package com.example.mi

import android.content.ContentValues.TAG
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mi.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email = binding.editTextEmailSignUp.text.toString()
            val password = binding.editTextPasswordSignUp.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Firebase Authentication을 이용한 회원가입
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 시
                            Toast.makeText(
                                this, "회원가입 성공",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Firestore에 데이터 저장
                            val uid = auth.currentUser?.uid
                            uid?.let {
                                saveDataToFirestore(it)
                                saveFlowerListToFirestore(it, 20) // flower_1부터 flower_20까지 저장
                            }

                            // 로그인 화면으로 이동
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // 회원가입 실패 시
                            Toast.makeText(
                                this, "회원가입 실패. 이메일 또는 비밀번호를 확인하세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // 이메일 또는 비밀번호가 비어있을 경우
                Toast.makeText(
                    this, "이메일과 비밀번호를 모두 입력하세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun saveDataToFirestore(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "MindPiece" to 0,
            "IslandName" to "마음의섬"
        )

        // 컬렉션("users")에 문서(uid)와 데이터(data)를 추가
        db.collection("users").document(uid)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun saveFlowerListToFirestore(uid: String, count: Int) {
        val db = FirebaseFirestore.getInstance()

        for (i in 1..count) {
            val flowerData = hashMapOf(
                "name" to "flower_${i.toString().padStart(2,'0')}",
                "price" to 50,
                "status" to 0,
                "x" to 0,
                "y" to 0
            )

            val flowerDocumentName = "flower_${i.toString().padStart(2,'0')}"

            db.collection("users").document(uid)
                .collection("FlowerList").document(flowerDocumentName)
                .set(flowerData)
                .addOnSuccessListener {
                    Log.d(TAG, "$flowerDocumentName document added for user: $uid")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding $flowerDocumentName document", e)
                }
        }
    }
}


package com.example.mi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mi.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

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
    }
}

package com.example.mi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mi.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            // 사용자가 입력한 이메일과 비밀번호 가져오기
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Firebase Authentication을 이용한 로그인
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시 MainActivity로 이동
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // 로그인 실패 시 사용자에게 메시지 표시
                        Toast.makeText(
                            baseContext, "로그인 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
        binding.btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}

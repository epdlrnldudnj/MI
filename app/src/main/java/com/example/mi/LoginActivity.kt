package com.example.mi

import android.content.Intent
import android.os.Bundle
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
            // 로그인 버튼 클릭 시 로그인 기능 수행
            // (앞서 알려준 로그인 기능 코드 사용)

            // 예시: 로그인 성공 시 MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
        binding.btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}

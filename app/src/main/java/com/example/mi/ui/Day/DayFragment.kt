package com.example.mi.ui.Day

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mi.R
import com.example.mi.databinding.FragmentDayBinding
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class DayFragment : Fragment(R.layout.fragment_day) {
    private val dayViewModel: DayViewModel by viewModels()
    private var binding: FragmentDayBinding? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler.postDelayed(this, 1000) // 1초마다 업데이트
        }
    }


    override fun onDestroyView() {
        handler.removeCallbacks(updateRunnable)
        super.onDestroyView()
    }

    private fun updateDateTime() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTimeString = dateFormat.format(Date())
        binding?.dayDate?.text = dateTimeString
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDayBinding.bind(view)

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDayBinding.bind(view)
        handler.post(updateRunnable)

        binding?.btnMoodBox?.setOnClickListener {
            showMoodSelector()
        }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Use the uri to load the image into the ImageView
                binding?.btnAddPhoto?.setImageURI(uri)
            }
        }

        binding?.btnAddPhoto?.setOnClickListener {
            pickImageFromGallery()
        }


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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            Toast.makeText(context, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndPickImage() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImageFromGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // 권한에 대한 추가적인 설명을 제공하고, 권한 요청을 다시 시도할 수 있습니다.
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }


    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }


    companion object {
        private const val PERMISSION_REQUEST_READ_STORAGE = 101
        private const val PICK_IMAGE_REQUEST = 102
    }
}

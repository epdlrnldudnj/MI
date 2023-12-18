package com.example.mi.ui.Day

import android.app.DatePickerDialog
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.R
import com.example.mi.databinding.FragmentDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayFragment : Fragment(R.layout.fragment_day) {
    private var binding: FragmentDayBinding? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChecklistAdapter
    private lateinit var rvGoal: RecyclerView
    private lateinit var goalAdapter: GoalAdapter
    private val goalsList: MutableList<Goal> = mutableListOf()

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler.postDelayed(this, 1000)
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
        handler.post(updateRunnable)

        binding?.btnMoodBox?.setOnClickListener {
            showMoodSelector()
        }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding?.btnAddPhoto?.setImageURI(uri)
            }
        }

        binding?.btnAddPhoto?.setOnClickListener {
            pickImageFromGallery()
        }

        recyclerView = view.findViewById(R.id.rvItems)
        rvGoal = view.findViewById(R.id.rvgoal)

        adapter = ChecklistAdapter()
        recyclerView.adapter = adapter

        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            showAddItemDialog()
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        goalAdapter = GoalAdapter(goalsList)
        rvGoal.adapter = goalAdapter
        rvGoal.layoutManager = LinearLayoutManager(context)

        val goalButton: ImageButton = view.findViewById(R.id.goalbutton)
        goalButton.setOnClickListener {
            showAddGoalDialog()
        }
        rvGoal.layoutManager = LinearLayoutManager(context)
    }

    private fun showAddGoalDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_long_term_goal, null)
        val tvGoalProgress = dialogView.findViewById<EditText>(R.id.tvGoalProgress)
        val etGoalPercentage = dialogView.findViewById<EditText>(R.id.etGoalPercentage)
        val btnSetEndDate = dialogView.findViewById<Button>(R.id.btnSetEndDate)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("목표 추가")
            .setView(dialogView)
            .setPositiveButton("저장", null) // 저장 버튼을 클릭한 후에 처리하기 위해 null로 설정
            .setNegativeButton("취소", null)
            .show()

        // 종료일 설정 버튼 클릭 이벤트 리스너를 설정합니다.
        btnSetEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    // 사용자가 날짜를 선택한 후의 동작을 여기에 추가합니다.
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // 선택한 날짜와 현재 날짜와의 차이를 계산하여 밀리초로 변환
                    val millis = selectedDate.timeInMillis

                    // 선택한 날짜를 TextView 또는 다른 UI 요소에 표시할 수 있습니다.
                    btnSetEndDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        // 저장 버튼 클릭 이벤트 리스너를 설정합니다.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val editTextGoalName = tvGoalProgress.text.toString()
            val editTextGoalPercentage = etGoalPercentage.text.toString()
            val editTextGoalDDay = btnSetEndDate.text.toString()

            if (editTextGoalName.isNotEmpty() && editTextGoalPercentage.isNotEmpty() && editTextGoalDDay.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(editTextGoalDDay)
                val millis = date?.time ?: 0

                if (millis > 0) {
                    val newGoal = Goal(editTextGoalName, editTextGoalPercentage.toInt(), millis)
                    goalsList.add(newGoal)
                    goalAdapter.notifyDataSetChanged()
                    dialog.dismiss() // 다이얼로그 닫기
                } else {
                    Toast.makeText(context, "유효한 날짜를 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_checklist_item, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextChecklistItem)

        AlertDialog.Builder(requireContext())
            .setTitle("체크리스트 항목 추가")
            .setView(dialogView)
            .setPositiveButton("저장") { dialog, which ->
                val itemText = editText.text.toString()
                if (itemText.isNotEmpty()) {
                    adapter.addItem(itemText)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showMoodSelector() {
        val moods = arrayOf("행복", "기쁨", "슬픔", "화남", "평온", "피곤", "체크")
        AlertDialog.Builder(requireContext())
            .setTitle("기분을 선택하세요")
            .setItems(moods) { dialog, which ->
                val selectedMood = moods[which]
                binding?.btnMoodBox?.setImageResource(getImageResourceForMood(selectedMood))
            }
            .show()
    }

    private fun getImageResourceForMood(mood: String): Int {
        return when (mood) {
            "화남" -> R.drawable.angry
            "기쁨" -> R.drawable.joy
            "행복" -> R.drawable.love
            "평온" -> R.drawable.clamdown
            "슬픔" -> R.drawable.depression
            "피곤" -> R.drawable.tiredness
            "체크" -> R.drawable.check
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

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    companion object {
        private const val PERMISSION_REQUEST_READ_STORAGE = 101
        private const val PICK_IMAGE_REQUEST = 102
    }
}

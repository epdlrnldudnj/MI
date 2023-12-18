package com.example.mi.ui.Day

import android.content.ContentValues.TAG
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
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
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var database: DatabaseReference

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
        // 'selectedDate'가 null이면 현재 날짜를 사용합니다.
        val selectedDate = arguments?.getString("selectedDate") ?: getCurrentDate()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val parsedDate = dateFormat.parse(selectedDate)
            val dateTimeString = dateFormat.format(parsedDate)
            binding?.dayDate?.text = dateTimeString
        } catch (e: ParseException) {
            // 날짜 파싱 실패 처리
            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        // FirebaseFirestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // FirebaseDatabase 인스턴스 초기화 (필요한 경우)
        database = FirebaseDatabase.getInstance().getReference("days")
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDayBinding.bind(view)
        database = FirebaseDatabase.getInstance().getReference("days")
        handler.post(updateRunnable)

        /// FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        // FirebaseFirestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // FirebaseDatabase 인스턴스 초기화 (필요한 경우)
        database = FirebaseDatabase.getInstance().getReference("days")

        updateDateTime()
        val currentDate = arguments?.getString("selectedDate") ?: getCurrentDate()
        // Bundle에서 선택된 날짜를 가져옵니다.
        val selectedDate = arguments?.getString("selectedDate")

        // 선택된 날짜를 사용하여 UI 업데이트
        updateUIForSelectedDate(selectedDate)

        binding?.btnMoodBox?.setOnClickListener {
            showMoodSelector()
        }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                updateMindPiece(10)
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
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun updateUIForSelectedDate(date: String?) {
        // 선택된 날짜에 대한 UI 업데이트 로직
        // 예: 선택된 날짜를 텍스트 뷰에 표시
        binding?.dayDate?.text = date
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
                    updateMindPiece(10)

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
                updateMindPiece(10)
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
                updateMindPiece(10)

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
    private fun saveData(date: String, data: DayData) {
        database.child(date).setValue(data)
            .addOnSuccessListener {
                // 데이터 저장 성공 처리
                Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // 데이터 저장 실패 처리
                Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData(date: String) {
        database.child(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(DayData::class.java)
                // 데이터 불러오기 성공, UI 업데이트
                updateUI(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 불러오기 실패 처리
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(data: DayData?) {
        data?.let {
            // 예: 텍스트뷰, 이미지뷰 업데이트
            //binding.textView.text = it.text
            //binding.imageView.loadImage(it.imageUrl) // 이미지 로드 함수는 별도 구현 필요
            // 기타 UI 업데이트
        }
    }
    data class DayData(
        val text: String? = null,
        val imageUrl: String? = null,
        val mood: String? = null
        // 기타 필요한 필드를 추가할 수 있습니다.
    )

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
                               // binding.piece.text = updatedMindPiece.toString()
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

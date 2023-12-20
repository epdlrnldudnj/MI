package com.example.mi.ui.Day

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mi.R
import com.example.mi.databinding.FragmentDayBinding
import com.example.mi.ui.Day.ChecklistAdapter
import com.example.mi.ui.Day.Goal
import com.example.mi.ui.Day.GoalAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private var selectedMood: String? = null

    private lateinit var database: DatabaseReference
    private lateinit var goalAdapter: GoalAdapter
    private val goalsList: MutableList<Goal> = mutableListOf()

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChecklistAdapter

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    private lateinit var rvGoal: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        val date = getCurrentDate() // 현재 날짜를 가져옴
        val dataPath = "DayData/$date" // 데이터베이스 경로 설정

// 실제 데이터 객체를 생성하고 해당 경로에 저장
        val dayData = DayData() // DayData 클래스는 실제 데이터 객체여야 함
        database.child(dataPath).setValue(dayData)


        setTodayAsDefaultDate()
        binding.dayDate.setOnClickListener {
            showDatePicker()
        }
        loadDayData(getCurrentDate())

        recyclerView = view.findViewById(R.id.rvItems)
        adapter = ChecklistAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            showAddItemDialog()
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.btnAddPhoto.setImageURI(uri)
                selectedImageUri = uri // 이미지 URI 업데이트
            }
        }
        binding.btnAddPhoto.setOnClickListener {
            pickImageFromGallery()
        }

        rvGoal = view.findViewById(R.id.rvgoal)
        goalAdapter = GoalAdapter(goalsList)
        rvGoal.adapter = goalAdapter
        rvGoal.layoutManager = LinearLayoutManager(requireContext())

        val goalButton: ImageButton = view.findViewById(R.id.goalbutton)
        goalButton.setOnClickListener {
            showAddGoalDialog()
        }

        binding.btnMoodBox.setOnClickListener {
            showMoodSelector()
        }

        binding.btnSetGoal.setOnClickListener {
            saveDayData()
        }
    }

    private fun setTodayAsDefaultDate() {
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(today.time)
        binding.dayDate.text = todayStr
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            binding.dayDate.text = dateStr

            // 화면 초기화 및 데이터 불러오기
            resetUI()
            loadDayData(dateStr)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadDayData(date: String) {
        database.child("DayData").child(date).get().addOnSuccessListener { dataSnapshot ->
            val data = dataSnapshot.getValue(DayData::class.java)
            if (data != null) {
                updateUI(data)
            } else {
                resetUI()
            }
        }.addOnFailureListener { e ->
            Log.e("DayFragment", "Error loading data", e)
            Toast.makeText(requireContext(), "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            resetUI()
        }
    }

    private fun updateUI(data: DayData?) {
        data?.let { dayData ->
            dayData.todoList?.let { todos ->
                adapter.updateItems(todos)
            }

            dayData.goals?.let { goals ->
                goalAdapter.updateGoals(goals)
            }

            binding.photostory.text = (dayData.photoStory ?: "") as Editable?

            data.photoUri?.let { uri ->
                Glide.with(this@DayFragment).load(uri).into(binding.btnAddPhoto)
            }
        }
    }

    private fun resetUI() {
        adapter.updateItems(listOf())
        goalsList.clear()
        goalAdapter.notifyDataSetChanged()
        binding.photostory.setText("")
        binding.btnMoodBox.setImageResource(R.drawable.check)
        binding.btnAddPhoto.setImageResource(R.drawable.check)
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

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun showAddGoalDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_long_term_goal, null)
        val tvGoalProgress = dialogView.findViewById<EditText>(R.id.tvGoalProgress)
        val etGoalPercentage = dialogView.findViewById<EditText>(R.id.etGoalPercentage)
        val btnSetEndDate = dialogView.findViewById<Button>(R.id.btnSetEndDate)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("목표 추가")
            .setView(dialogView)
            .setPositiveButton("저장", null)
            .setNegativeButton("취소", null)
            .show()

        btnSetEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                    btnSetEndDate.text = dateStr
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

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
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "유효한 날짜를 선택하세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMoodSelector() {
        val moods = arrayOf("행복", "기쁨", "슬픔", "화남", "평온", "피곤", "체크")
        AlertDialog.Builder(requireContext())
            .setTitle("기분을 선택하세요")
            .setItems(moods) { dialog, which ->
                selectedMood = moods[which]
                selectedMood?.let {
                    binding.btnMoodBox.setImageResource(getImageResourceForMood(it))
                }
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

    private fun saveDayData() {
        val date = getCurrentDate()
        val todoList = adapter.getItems()
        val photoUri = selectedImageUri?.toString() ?: ""
        val photoStory = binding.photostory.text.toString()
        val goals = goalsList
        val mood = selectedMood ?: ""

        val dayData = DayData(
            date = date,
            todoList = todoList,
            photoUri = photoUri,
            photoStory = photoStory,
            goals = goals,
            mood = mood
        )

        // Firebase에 데이터 저장
        database.child("DayData").child(date).setValue(dayData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("DayFragment", "Error saving data", e)
                Toast.makeText(requireContext(), "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    data class DayData(
        val date: String? = null,
        val todoList: List<String>? = null,
        val photoUri: String? = null,
        val photoStory: String? = null,
        val goals: List<Goal>? = null,
        val mood: String? = null
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.mi.ui.Day

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mi.R
import com.example.mi.databinding.FragmentDayBinding
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private var selectedMood: String? = null

    private lateinit var dbHelper: MyDatabaseHelper
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

        recyclerView = view.findViewById(R.id.rvItems)
        adapter = ChecklistAdapter() // 이 부분을 맨 위로 이동
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rvGoal = view.findViewById(R.id.rvgoal)

        goalAdapter = GoalAdapter()
        rvGoal.adapter = goalAdapter
        rvGoal.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = MyDatabaseHelper(requireContext())

        setTodayAsDefaultDate()
        binding.dayDate.setOnClickListener {
            showDatePicker()
        }
        loadDayData(getCurrentDate())

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
        val dayData = dbHelper.getDayDataByDate(date)
        Log.d("DayFragment", "Loading data for date: $date, result: $dayData")
        if (dayData != null) {
            updateUI(dayData)
        } else {
            resetUI()
        }
    }
    private fun updateUI(dayData: DayData) {
        adapter.updateItems(dayData.todoList ?: listOf())
        goalAdapter.updateGoals(dayData.goals ?: listOf())

        Log.d("DayFragment", "Goals JSON: ${dayData.goals}")

        binding.photostory.text = Editable.Factory.getInstance().newEditable(dayData.photoStory ?: "")
        dayData.photoUri?.let { uri ->
            Glide.with(this).load(Uri.parse(uri)).into(binding.btnAddPhoto)
        }
    }



    private fun resetUI() {
        if (::adapter.isInitialized) {
            adapter.updateItems(listOf())
        }
        if (::goalAdapter.isInitialized) {
            goalsList.clear()
            goalAdapter.notifyDataSetChanged()
        }
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
            else -> R.drawable.add_emoji
        }
    }

    private fun saveDayData() {
        val gson = Gson()
        val date = binding.dayDate.text.toString()
        val todoList = adapter.getItems()
        val photoUri = selectedImageUri?.toString()
        val photoStory = binding.photostory.text.toString()
        val goalsJson = gson.toJson(goalsList) // Goal 객체 목록을 JSON 문자열로 변환합니다.
        val mood = selectedMood

        // JSON 문자열이 아니라, 직렬화된 JSON 문자열을 DayData 객체에 저장합니다.
        val dayData = DayData(date, todoList, photoUri, photoStory, goalsList, mood)
        val dayDataManager = DayDataManager(requireContext())
        dayDataManager.saveDayData(dayData)
        Toast.makeText(context, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        Log.d("DayFragment", "Loading data for date: $date, result: $dayData")

        try {
            val dayData = DayData(date, todoList, photoUri, photoStory, goalsList, mood)
            val dayDataManager = DayDataManager(requireContext())
            dayDataManager.saveDayData(dayData)
            Toast.makeText(context, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("DayFragment", "Error saving day data", e)
            Toast.makeText(context, "데이터 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
data class DayData(
    val date: String,
    val todoList: List<String>?,
    val photoUri: String?,
    val photoStory: String?,
    val goals: List<Goal>?, // List<Goal> 타입으로 선언
    val mood: String?
)

class DayDataManager(private val context: Context) {
    private val dbHelper = MyDatabaseHelper(context)

    fun saveDayData(dayData: DayData) {
        // DayData 객체를 데이터베이스에 저장
        dbHelper.saveDayData(dayData)
    }

    fun getDayDataByDate(date: String): DayData? {
        // 주어진 날짜에 해당하는 DayData 객체를 데이터베이스에서 검색
        return dbHelper.getDayDataByDate(date)
    }
}


class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // 데이터베이스 버전 및 테이블 구조 정의
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase.db"
        // ... (기타 필요한 상수 정의)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 테이블 생성 SQL 문
        val CREATE_DAY_DATA_TABLE = """
        CREATE TABLE day_data (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            date TEXT,
            todoList TEXT,
            photoUri TEXT,
            photoStory TEXT,
            goals TEXT,
            mood TEXT
        )
    """.trimIndent()

        // 데이터베이스에 테이블 생성
        db?.execSQL(CREATE_DAY_DATA_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 스키마가 변경되었을 때 기존 테이블을 삭제하고 새로운 테이블을 생성합니다.
        // 실제 애플리케이션에서는 사용자의 데이터를 보존하기 위해 마이그레이션 로직을 구현해야 할 수 있습니다.
        db?.execSQL("DROP TABLE IF EXISTS day_data")
        onCreate(db)
    }


    @SuppressLint("Range")
    fun saveDayData(dayData: DayData) {
        val db = this.writableDatabase
        val gson = Gson()
        val contentValues = ContentValues()

        // DayData 객체의 필드를 ContentValues에 채움
        contentValues.put("date", dayData.date)
        contentValues.put("todoList", gson.toJson(dayData.todoList))
        contentValues.put("photoUri", dayData.photoUri)
        contentValues.put("photoStory", dayData.photoStory)
        contentValues.put("goals", gson.toJson(dayData.goals)) // List<Goal>을 JSON 문자열로 변환
        contentValues.put("mood", dayData.mood)

        // 해당 날짜에 대한 데이터가 이미 있는지 확인
        val cursor = db.query("day_data", arrayOf("id"), "date = ?", arrayOf(dayData.date), null, null, null)
        val exists = cursor.moveToFirst()

        if (exists) {
            // 이미 데이터가 있으면 업데이트
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            db.update("day_data", contentValues, "id = ?", arrayOf(id.toString()))
        } else {
            // 데이터가 없으면 새로 삽입
            db.insert("day_data", null, contentValues)
        }

        // 리소스 정리
        cursor.close()
        db.close()
    }


    @SuppressLint("Range")
    fun getDayDataByDate(date: String): DayData? {
        val db = this.readableDatabase
        val cursor = db.query("day_data", null, "date = ?", arrayOf(date), null, null, null)

        if (cursor.moveToFirst()) {
            val gson = Gson()
            // 커서에서 데이터 추출 및 DayData 객체 생성
            val retrievedDate = cursor.getString(cursor.getColumnIndex("date"))
            val todoListJson = cursor.getString(cursor.getColumnIndex("todoList"))
            val photoUri = cursor.getString(cursor.getColumnIndex("photoUri"))
            val photoStory = cursor.getString(cursor.getColumnIndex("photoStory"))
            val goalsJson = cursor.getString(cursor.getColumnIndex("goals"))
            val mood = cursor.getString(cursor.getColumnIndex("mood"))

            // JSON 문자열을 List<Goal>로 변환
            val goalsType = object : TypeToken<List<Goal>>() {}.type
            val goals = gson.fromJson<List<Goal>>(goalsJson, goalsType)

            cursor.close()
            db.close()
            return DayData(retrievedDate, gson.fromJson(todoListJson, object : TypeToken<List<String>>() {}.type), photoUri, photoStory, goals, mood)
        }

        cursor.close()
        db.close()
        return null
    }
}


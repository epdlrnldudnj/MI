package com.example.mi.ui.Day

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.R
import com.google.gson.Gson
import java.util.Calendar

class GoalAdapter : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {
    private val goals = mutableListOf<Goal>()  // 데이터 목록

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal)
    }

    // 아이템 갯수 반환
    override fun getItemCount(): Int = goals.size

    // 목표 정보를 표시하는 뷰홀더
    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvGoalName: TextView = itemView.findViewById(R.id.tvGoalName)
        private val tvGoalPercentage: TextView = itemView.findViewById(R.id.tvGoalPercentage)
        private val tvGoalDDay: TextView = itemView.findViewById(R.id.tvGoalDDay)

        fun bind(goal: Goal) {
            tvGoalName.text = goal.name
            tvGoalPercentage.text = "진행도: ${goal.percentage}%"
            val dDay = calculateDDay(goal.endDate)
            tvGoalDDay.text = "D-$dDay"
        }
    }

    // D-Day 계산
    private fun calculateDDay(endDate: Long): Int {
        val today = Calendar.getInstance().timeInMillis
        val dDayInMillis = endDate - today
        return (dDayInMillis / (24 * 60 * 60 * 1000)).toInt() // 일수로 변환
    }

    // 목표 목록 업데이트
    fun updateGoals(newGoals: List<Goal>) {
        goals.clear()
        goals.addAll(newGoals)
        notifyDataSetChanged()
    }
}

// Goal 데이터 클래스
data class Goal(val name: String, val percentage: Int, val endDate: Long) {
    companion object {
        // 문자열에서 Goal 객체로 변환
        fun fromString(json: String): Goal {
            return Gson().fromJson(json, Goal::class.java)
        }

        // Goal 객체에서 문자열로 변환
        fun toString(goal: Goal): String {
            return Gson().toJson(goal)
        }
    }
}

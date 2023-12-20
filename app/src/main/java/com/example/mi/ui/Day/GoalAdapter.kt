package com.example.mi.ui.Day
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.R
import java.util.Calendar

class GoalAdapter(private var goals: List<Goal>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal)
    }

    override fun getItemCount(): Int = goals.size

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

    private fun calculateDDay(endDate: Long): Int {
        val today = Calendar.getInstance().timeInMillis
        val dDayInMillis = endDate - today
        return (dDayInMillis / (24 * 60 * 60 * 1000)).toInt() // 일수로 변환
    }


    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals.toList() // goals 변수를 새 목표 리스트로 대체
        notifyDataSetChanged() // 어댑터에게 데이터 변경을 알림
    }

}

data class Goal(
    val name: String, // 목표 이름
    val percentage: Int, // 진행도 (0-100)
    val endDate: Long // 종료일시 (Unix 타임스탬프)
)

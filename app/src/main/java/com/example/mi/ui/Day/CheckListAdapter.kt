package com.example.mi.ui.Day
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.R
class ChecklistAdapter : RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {

    private var items = mutableListOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
        // 체크박스 상태 설정 (예제로서 항상 false로 설정)
        holder.checkBox.isChecked = false

        // 체크박스 클릭 리스너 설정
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // 체크박스 상태 변경 시 처리할 로직
            // 예: 해당 아이템의 상태 업데이트, 데이터베이스 업데이트 등
        }

    }

    override fun getItemCount() = items.size

    fun addItem(item: String) {
        items.add(item)
        notifyDataSetChanged()
    }
}

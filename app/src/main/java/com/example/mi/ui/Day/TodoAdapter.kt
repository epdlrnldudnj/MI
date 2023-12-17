package com.example.mi.ui.Day

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.R

class TodoAdapter(private var items: List<TodoItem>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    // ViewHolder 클래스
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 뷰 바인딩 설정 (예: TextView, CheckBox)
        val textView: TextView = view.findViewById(R.id.Todo)
        // ... 기타 뷰 ...
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.text
        // ... 항목 데이터 바인딩 ...
    }

    override fun getItemCount() = items.size

    // 항목 데이터 업데이트 메서드
    fun updateItems(newItems: List<TodoItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

data class TodoItem(val text: String, val isChecked: Boolean)

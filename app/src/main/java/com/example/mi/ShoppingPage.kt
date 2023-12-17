package com.example.mi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.MindPiece
import com.example.mi.databinding.ActivityShoppingPageBinding
import com.example.mi.ui.dashboard.IslandViewModel

class ShoppingPage : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingPageBinding

    data class Flowers(val imageId: Int, val price: Int, var status: Int)

    val price1 = 50
    val price2 = 100
    private var flowersClass = listOf(
        Flowers(R.drawable.flower_1, price1, 0),
        Flowers(R.drawable.flower_2, price1, 0),
        Flowers(R.drawable.flower_3, price1, 0),
        Flowers(R.drawable.flower_4, price1, 0),
        Flowers(R.drawable.flower_5, price1, 0),
        Flowers(R.drawable.flower_6, price1, 0),
        Flowers(R.drawable.flower_7, price1, 0),
        Flowers(R.drawable.flower_8, price1, 0),
        Flowers(R.drawable.flower_9, price1, 0),
        Flowers(R.drawable.flower_10, price1, 0),
        Flowers(R.drawable.flower_11, price1, 0),
        Flowers(R.drawable.flower_12, price1, 0),
        Flowers(R.drawable.flower_13, price1, 0),
        Flowers(R.drawable.flower_14, price1, 0),
        Flowers(R.drawable.flower_15, price1, 0),
        Flowers(R.drawable.flower_16, price2, 0),
        Flowers(R.drawable.flower_17, price2, 0),
        Flowers(R.drawable.flower_18, price2, 0),
        Flowers(R.drawable.flower_19, price2, 0),
        Flowers(R.drawable.flower_20, price2, 0),
    )

    class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flowerImage: ImageView = itemView.findViewById(R.id.imageView)
        val flowerPrice: TextView = itemView.findViewById(R.id.priceTextView)
        val flowerBuy: Button = itemView.findViewById(R.id.buyButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val returnButton: Button = binding.button

        returnButton.setOnClickListener {
            val intent = Intent(this, IslandViewModel::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = FlowerAdapter(flowersClass)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    class FlowerAdapter(private val flowers: List<Flowers>) :
        RecyclerView.Adapter<FlowerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_flower, parent, false)
            return FlowerViewHolder(view)
        }

        override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
            val flower = flowers[position]

            holder.flowerImage.setImageResource(flower.imageId)
            holder.flowerPrice.text = "${flower.price}"
            if (flower.status != 0) {
                holder.flowerBuy.text = "구매완료"
            }

            // Buy 버튼에 대한 클릭 이벤트 처리 추가 (원하는 동작으로 수정)
            holder.flowerBuy.setOnClickListener {
                // Buy 버튼이 눌렸을 때 수행할 동작 추가
                // 예: Toast 메시지 출력
                Toast.makeText(
                    holder.itemView.context,
                    "상품을 구매했습니다. ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
                flower.status = 1
                holder.flowerBuy.text = "구매완료"

            }
        }

        override fun getItemCount(): Int {
            return flowers.size
        }
    }
}

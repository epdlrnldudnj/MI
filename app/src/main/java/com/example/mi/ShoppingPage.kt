package com.example.mi

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ActivityShoppingPageBinding
import com.example.mi.ui.dashboard.IslandViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShoppingPage : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    data class Flowers(val imageId: Int, val price: Int, var status: Int)

    class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flowerImage: ImageView = itemView.findViewById(R.id.imageView)
        val flowerPrice: TextView = itemView.findViewById(R.id.priceTextView)
        val flowerBuy: Button = itemView.findViewById(R.id.buyButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val returnButton: Button = binding.button
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = FlowerAdapter(ArrayList())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Fetch flower data from Firebase
        fetchFlowerData(adapter)
    }

    private fun fetchFlowerData(adapter: FlowerAdapter) {
        val uid = auth.currentUser?.uid
        uid?.let {
            // Fetch flower data from Firestore
            db.collection("users").document(uid).collection("FlowerList")
                .get()
                .addOnSuccessListener { result ->
                    val flowersList = mutableListOf<Flowers>()

                    for (document in result) {
                        val imageName = document.getString("name") ?: ""
                        val price = document.getLong("price")?.toInt() ?: 0
                        val status = document.getLong("status")?.toInt() ?: 0

                        // Get the image resource ID dynamically based on the image name
                        val imageId = resources.getIdentifier(imageName, "drawable", packageName)

                        val flower = Flowers(imageId, price, status)
                        flowersList.add(flower)
                    }
                    adapter.setFlowers(flowersList)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    class FlowerAdapter(private val flowers: MutableList<Flowers>) :
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
            if (flower.status == 0) {
                holder.flowerBuy.text = "구매하기"
            }else{
                holder.flowerBuy.text = "구매완료"
            }

            holder.flowerBuy.setOnClickListener {
                // Buy 버튼이 눌렸을 때 수행할 동작 추가
                Toast.makeText(
                    holder.itemView.context,
                    "상품을 구매했습니다. ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()

                // flower.status를 1로 업데이트
                flower.status = 1

                // 파이어베이스 업데이트
                updateFirebaseStatus(flower, position)

                holder.flowerBuy.text = "구매완료"
            }
        }

        override fun getItemCount(): Int {
            return flowers.size
        }

        fun setFlowers(flowersList: List<Flowers>) {
            flowers.clear()
            flowers.addAll(flowersList)
            notifyDataSetChanged()
        }
        private fun updateFirebaseStatus(flower: Flowers, position: Int) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let {
                val documentRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("FlowerList")
                    .document("flower_${(position + 1).toString().padStart(2,'0')}")

                documentRef
                    .update("status", flower.status)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            }
        }
    }
}

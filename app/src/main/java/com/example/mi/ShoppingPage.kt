package com.example.mi

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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

        val returnButton: ImageButton = binding.button
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
            } else {
                holder.flowerBuy.text = "구매완료"
                holder.flowerBuy.isEnabled = false  // 이미 구매 완료된 경우 버튼 비활성화
            }

            holder.flowerBuy.setOnClickListener {
                // 이미 구매 완료된 경우 처리하지 않음
                if (flower.status == 1) {
                    return@setOnClickListener
                }

                // 현재 사용자의 MindPiece 값 가져오기
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                uid?.let {
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val mindPiece = documentSnapshot.getLong("MindPiece") ?: 0

                            // MindPiece가 50 이상이면 구매 처리
                            if (mindPiece >= 50) {
                                // MindPiece를 50 감소
                                val updatedMindPiece = mindPiece - 50
                                updateMindPieceInDatabase(updatedMindPiece)

                                // flower.status를 1로 업데이트
                                flower.status = 1

                                // 파이어베이스 업데이트
                                updateFirebaseStatus(flower, position)

                                holder.flowerBuy.text = "구매완료"
                                holder.flowerBuy.isEnabled = false  // 이미 구매 완료된 경우 버튼 비활성화

                                Toast.makeText(
                                    holder.itemView.context,
                                    "상품을 구매했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // MindPiece가 50 미만인 경우 Toast 메시지 표시
                                Toast.makeText(
                                    holder.itemView.context,
                                    "마음의 조각이 부족합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error getting MindPiece", e)
                        }
                }
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
        private fun updateMindPieceInDatabase(updatedMindPiece: Long) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let {
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .update("MindPiece", updatedMindPiece)
                    .addOnSuccessListener {
                        Log.d(TAG, "MindPiece successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating MindPiece", e)
                    }
            }
        }
    }
}

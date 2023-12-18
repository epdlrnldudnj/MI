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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mi.databinding.ActivityStoragePageBinding
import com.example.mi.ui.dashboard.IslandViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StoragePage : AppCompatActivity() {

    private lateinit var binding: ActivityStoragePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    data class Flowers(val imageId: Int, var status: Int, val name: String)

    class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flowerImage: ImageView = itemView.findViewById(R.id.imageView)
        val flowerButton: Button = itemView.findViewById(R.id.flowerbutton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoragePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase instances
        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!

        val returnButton: ImageButton = binding.button
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = FlowerAdapter(ArrayList())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Fetch flower data from Firebase (only with status != 0)
        fetchFlowerData(adapter)
    }

    private fun fetchFlowerData(adapter: FlowerAdapter) {
        val uid = currentUser.uid
        uid?.let {
            // Fetch flower data from Firestore with status != 0
            db.collection("users").document(uid).collection("FlowerList")
                .whereNotEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    val flowersList = mutableListOf<Flowers>()

                    for (document in result) {
                        val imageName = document.getString("name") ?: ""
                        val status = document.getLong("status")?.toInt() ?: 0

                        // Get the image resource ID dynamically based on the image name
                        val imageId = resources.getIdentifier(imageName, "drawable", packageName)

                        val flower = Flowers(imageId, status, imageName)
                        flowersList.add(flower)
                    }
                    adapter.setFlowers(flowersList)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    // Rest of the FlowerAdapter and ViewHolder implementation...
    class FlowerAdapter(private val flowers: MutableList<Flowers>) :
        RecyclerView.Adapter<FlowerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_flower_bought, parent, false)
            return FlowerViewHolder(view)
        }

        override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
            val flower = flowers[position]

            holder.flowerImage.setImageResource(flower.imageId)

            if (flower.status == 1) {
                holder.flowerButton.text = "배치하기"
            } else if (flower.status == 2) {
                holder.flowerButton.text = "제거하기"
            }

            holder.flowerButton.setOnClickListener {
                // 버튼이 눌렸을 때 수행할 동작 추가
                // 예: 상세 정보 보기, 삭제 등
                Toast.makeText(
                    holder.itemView.context,
                    "상세 정보 보기: ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()

                // 데이터 업데이트
                val newStatus = if (flower.status == 1) 2 else 1
                updateFirebaseData(flower.name, newStatus)

                // RecyclerView 갱신
                flower.status = newStatus
                notifyDataSetChanged()
            }
        }

        private fun updateFirebaseData(name: String, newStatus: Int) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let {
                // Update flower data in Firestore
                val documentRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("FlowerList")
                    .whereEqualTo("name", name)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                document.reference.update("status", newStatus)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error updating document", e)
                                    }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.exception)
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
    }

}

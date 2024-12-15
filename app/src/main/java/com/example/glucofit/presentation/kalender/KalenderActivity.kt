package com.example.glucofit.presentation.kalender

import BloodSugarAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.glucofit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class KalenderActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvNoData: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var bloodSugarAdapter: BloodSugarAdapter
    private val bloodSugarList = mutableListOf<BloodSugarData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalender)

        calendarView = findViewById(R.id.calendarView)
        tvNoData = findViewById(R.id.tvNoData)
        recyclerView = findViewById(R.id.recyclerView)
        val btnCloseDetail = findViewById<ImageButton>(R.id.btn_close_detail)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        bloodSugarAdapter = BloodSugarAdapter(bloodSugarList)
        recyclerView.adapter = bloodSugarAdapter

        // Set current date on CalendarView
        calendarView.setDate(System.currentTimeMillis(), true, true)

        // Fetch data for the current date
        val currentDate = getCurrentDate()
        fetchBloodSugarData(currentDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchBloodSugarData(selectedDate)
        }

        btnCloseDetail.setOnClickListener {
            finish() // Close the activity
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    private fun fetchBloodSugarData(date: String) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userBloodSugarRef = database.child("bloodSugarData").child(userId)

            userBloodSugarRef.orderByChild("date").equalTo(date)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        bloodSugarList.clear()
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val bloodSugarData = snapshot.getValue(BloodSugarData::class.java)
                                if (bloodSugarData != null) {
                                    bloodSugarList.add(bloodSugarData)
                                }
                            }
                            tvNoData.visibility = View.GONE
                        } else {
                            tvNoData.visibility = View.VISIBLE
                        }
                        bloodSugarAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("KalenderActivity", "Error fetching data", databaseError.toException())
                        Toast.makeText(this@KalenderActivity, "Failed to fetch data.", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    data class BloodSugarData(
        val bloodSugar: String? = null,
        val date: String? = null,
        val email: String? = null,
        val result: String? = null
    )
}

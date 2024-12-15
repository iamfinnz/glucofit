package com.example.glucofit.presentation.cek

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glucofit.R
import com.example.glucofit.databinding.ActivityCekBinding
import com.example.glucofit.presentation.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CekActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCekBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.btnCloseCek.setOnClickListener {
            finish() // Close the activity
        }

        setupGenderAutoComplete()
        setupDiabetesHistoryAutoComplete()
        setupPhysicalActivityAutoComplete() // Setup for physical activity
        setupLastMealTimeAutoComplete() // Setup for last meal time

        binding.btnCek.setOnClickListener {
            Log.d("CekActivity", "Button Cek Gula Darah clicked")
            if (validateForm()) {
                val result = checkBloodSugar()
                // Save data to Firebase and then show the dialog
                saveDataToFirebase(result)
            }
        }
    }

    private fun setupGenderAutoComplete() {
        val genderOptions = arrayOf("Laki-Laki", "Perempuan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)

        // Menampilkan dropdown saat AutoCompleteTextView diklik
        binding.etGender.setOnTouchListener { _, _ ->
            binding.etGender.showDropDown()
            false // Mengembalikan false agar event touch diteruskan
        }
    }

    private fun setupDiabetesHistoryAutoComplete() {
        val historyOptions = arrayOf("Ada", "Tidak Ada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, historyOptions)
        binding.etDiabetesHistory.setAdapter(adapter)

        // Menampilkan dropdown saat AutoCompleteTextView diklik
        binding.etDiabetesHistory.setOnTouchListener { _, _ ->
            binding.etDiabetesHistory.showDropDown()
            false // Mengembalikan false agar event touch diteruskan
        }
    }

    private fun setupPhysicalActivityAutoComplete() {
        val activityOptions = arrayOf("2-3 kali seminggu", "> 2-3 kali seminggu", "< 2-3 kali seminggu", "Tidak Berolahraga")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, activityOptions)
        binding.etPhysicalActivity.setAdapter(adapter)

        // Menampilkan dropdown saat AutoCompleteTextView diklik
        binding.etPhysicalActivity.setOnTouchListener { _, _ ->
            binding.etPhysicalActivity.showDropDown()
            false // Mengembalikan false agar event touch diteruskan
        }
    }

    private fun setupLastMealTimeAutoComplete() {
        val mealTimeOptions = arrayOf("< 2 jam", "2-8 jam", "> 8 jam")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mealTimeOptions)
        binding.etLastMealTime.setAdapter(adapter)

        // Menampilkan dropdown saat AutoCompleteTextView diklik
        binding.etLastMealTime.setOnTouchListener { _, _ ->
            binding.etLastMealTime.showDropDown()
            false // Mengembalikan false agar event touch diteruskan
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etName.text.isNullOrEmpty()) {
            binding.etName.error = "Nama Lengkap harus diisi"
            isValid = false
        } else {
            binding.etName.error = null
        }

        if (binding.etAge.text.isNullOrEmpty()) {
            binding.etAge.error = "Umur harus diisi"
            isValid = false
        } else {
            binding.etAge.error = null
        }

        if (binding.etGender.text.isNullOrEmpty()) {
            binding.etGender.error = "Jenis Kelamin harus diisi"
            isValid = false
        } else {
            binding.etGender.error = null
        }

        if (binding.etWeight.text.isNullOrEmpty()) {
            binding.etWeight.error = "Berat Badan harus diisi"
            isValid = false
        } else {
            binding.etWeight.error = null
        }

        if (binding.etHeight.text.isNullOrEmpty()) {
            binding.etHeight.error = "Tinggi Badan harus diisi"
            isValid = false
        } else {
            binding.etHeight.error = null
        }

        if (binding.etBloodSugar.text.isNullOrEmpty()) {
            binding.etBloodSugar.error = "Kadar Gula Darah harus diisi"
            isValid = false
        } else {
            binding.etBloodSugar.error = null
        }

        if (binding.etDiabetesHistory.text.isNullOrEmpty()) {
            binding.etDiabetesHistory.error = "Riwayat Diabetes harus diisi"
            isValid = false
        } else {
            binding.etDiabetesHistory.error = null
        }

        if (binding.etPhysicalActivity.text.isNullOrEmpty()) {
            binding.etPhysicalActivity.error = "Aktivitas Fisik harus diisi"
            isValid = false
        } else {
            binding.etPhysicalActivity.error = null
        }

        if (binding.etLastMealTime.text.isNullOrEmpty()) {
            binding.etLastMealTime.error = "Jam Terakhir Makan harus diisi"
            isValid = false
        } else {
            binding.etLastMealTime.error = null
        }

        return isValid
    }

    private fun checkBloodSugar(): String {
        val bloodSugar = binding.etBloodSugar.text.toString().toFloatOrNull()

        return if (bloodSugar != null) {
            when {
                bloodSugar >= 200 -> "Diabetes"
                bloodSugar in 140.0..199.9 -> "Pre-Diabetes"
                else -> "Tidak Diabetes"
            }
        } else {
            "Mohon masukkan kadar gula darah yang valid."
        }
    }

    private fun saveDataToFirebase(result: String) {
        val bloodSugar = binding.etBloodSugar.text.toString()
        val physicalActivity = binding.etPhysicalActivity.text.toString() // Ambil aktivitas fisik
        val lastMealTime = binding.etLastMealTime.text.toString() // Ambil jam terakhir makan
        val age = binding.etAge.text.toString().toIntOrNull() // Ambil umur
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val user = auth.currentUser

        // Kategorisasi umur
        val ageCategory = when {
            age != null && age in 9..18 -> "Remaja"
            age != null && age in 19..60 -> "Dewasa"
            else -> "Tidak Diketahui"
        }

        if (user != null) {
            Log.d("CekActivity", "User is logged in")
            val userId = user.uid
            val userEmail = user.email ?: "Unknown"
            val userBloodSugarRef = database.child("bloodSugarData").child(userId).push()

            val bloodSugarData = mapOf(
                "bloodSugar" to bloodSugar,
                "date" to currentDate,
                "email" to userEmail,
                "result" to result,
                "physicalActivity" to physicalActivity, // Menyimpan aktivitas fisik
                "lastMealTime" to lastMealTime, // Menyimpan jam terakhir makan
                "ageCategory" to ageCategory // Menyimpan kategori umur
            )

            Log.d("CekActivity", "Data to be saved: $bloodSugarData")
            Log.d("CekActivity", "Database reference: ${userBloodSugarRef.key}")

            userBloodSugarRef.setValue(bloodSugarData)
                .addOnSuccessListener {
                    Log.d("CekActivity", "Data successfully saved to Firebase")
                    // Data saved successfully, show the dialog
                    showDialog(result)
                }
                .addOnFailureListener { exception ->
                    Log.e("CekActivity", "Failed to save data to Firebase", exception)
                    // Handle failure
                    Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("CekActivity", "Data write complete")
                    } else {
                        Log.e("CekActivity", "Data write not successful", task.exception)
                    }
                }
        } else {
            Log.d("CekActivity", "User is not logged in")
            Toast.makeText(this, "User belum login.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog(result: String) {
        val dialogView = layoutInflater.inflate(R.layout.layout_dialog_success, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = result

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
                // Navigate back to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close CekActivity
            }
            .setNeutralButton(null, null)

        val titleView = layoutInflater.inflate(R.layout.layout_dialog_title, null)
        val titleTextView = titleView.findViewById<TextView>(R.id.dialogTitle)
        titleTextView.text = "Hasil Pemeriksaan"

        val alertDialog = dialogBuilder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(resources.getColor(android.R.color.white))
                setBackgroundColor(resources.getColor(R.color.colorSuccess))
                setPadding(16, 8, 16, 8)
                textSize = 12f
            }
        }

        alertDialog.setCustomTitle(titleView)
        alertDialog.show()
    }
}

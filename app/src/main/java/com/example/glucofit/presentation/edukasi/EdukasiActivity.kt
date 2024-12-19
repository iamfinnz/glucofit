package com.example.glucofit.presentation.edukasi

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glucofit.R
import android.text.Html

class EdukasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi)

        // Ambil data dari intent
        val isRemaja = intent.getBooleanExtra("isRemaja", false)
        val isDewasa = intent.getBooleanExtra("isDewasa", false)
        val hasilGulaDarah = intent.getStringExtra("hasilGulaDarah")

        // Menampilkan teks yang sesuai
        val tvEdukasiTitle = findViewById<TextView>(R.id.tvEdukasiTitle)
        val tvEdukasiDescription = findViewById<TextView>(R.id.tvEdukasiDescription)
        val btnCloseEdukasi = findViewById<ImageButton>(R.id.btn_close_edukasi) // Ambil tombol kembali

        // Cek kondisi untuk menampilkan teks yang sesuai
        if (isRemaja && (hasilGulaDarah == "Prediabetes" || hasilGulaDarah == "Prediabetes (Resiko Tinggi Diabetes Melitus)")) {
            tvEdukasiTitle.text = "Pencegahan Diabetes & Monitor Kadar Gula Darah"
            tvEdukasiDescription.text = Html.fromHtml(
                getString(R.string.pencegahan_diabetes_pediabetes),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else if (isRemaja && (hasilGulaDarah == "Diabetes")) {
            tvEdukasiTitle.text = "Konsultasikan Dengan Dokter / Pergi ke Pelayanan Kesehatan"
            tvEdukasiDescription.text = Html.fromHtml(getString(R.string.diabetes_remaja), Html.FROM_HTML_MODE_COMPACT)
        } else if (isRemaja && (hasilGulaDarah == "Normal" || hasilGulaDarah == "Normal (Memiliki Resiko Prediabetes)")) {
            tvEdukasiTitle.text = "Pencegahan Diabetes"
            tvEdukasiDescription.text = Html.fromHtml(getString(R.string.pencegahan_diabetes_remaja), Html.FROM_HTML_MODE_COMPACT)
        } else if (isDewasa && (hasilGulaDarah == "Prediabetes" || hasilGulaDarah == "Prediabetes (Resiko Tinggi Diabetes Melitus)")) {
            tvEdukasiTitle.text = "Pencegahan Diabetes & Monitor Kadar Gula Darah"
            tvEdukasiDescription.text = Html.fromHtml(getString(R.string.pencegahan_diabetes_dewasa_prediabetes), Html.FROM_HTML_MODE_COMPACT)
        } else if (isDewasa && (hasilGulaDarah == "Diabetes")) {
            tvEdukasiTitle.text = "Konsultasikan Dengan Dokter / Pergi ke Pelayanan Kesehatan"
            tvEdukasiDescription.text = Html.fromHtml(getString(R.string.konsultasi_diabetes_dewasa), Html.FROM_HTML_MODE_COMPACT)
        } else if (isDewasa && (hasilGulaDarah == "Normal" || hasilGulaDarah == "Normal (Memiliki Resiko Prediabetes)")) {
            tvEdukasiTitle.text = "Pencegahan Diabetes"
            tvEdukasiDescription.text = Html.fromHtml(getString(R.string.pencegahan_diabetes_dewasa), Html.FROM_HTML_MODE_COMPACT)
        } else {
            tvEdukasiTitle.text = "Informasi Edukasi"
            tvEdukasiDescription.text = "Hello World" // Teks default jika kondisi tidak terpenuhi
        }

        // Set listener untuk tombol kembali
        btnCloseEdukasi.setOnClickListener {
            finish() // Menutup aktivitas ini dan kembali ke aktivitas sebelumnya
        }
    }
}

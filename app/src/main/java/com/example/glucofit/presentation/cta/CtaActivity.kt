package com.example.glucofit.presentation.cta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glucofit.databinding.ActivityCtaBinding
import com.example.glucofit.presentation.login.LoginActivity
import com.example.glucofit.presentation.register.RegisterActivity

class CtaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCtaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCtaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.btnCloseCta.setOnClickListener { finish() }
        binding.btnDaftarAkun.setOnClickListener { openRegisterActivty() }
        binding.btnClaim.setOnClickListener { openLoginActivity() }
    }

    private fun openRegisterActivty() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

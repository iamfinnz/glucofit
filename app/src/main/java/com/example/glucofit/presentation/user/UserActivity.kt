package com.example.glucofit.presentation.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_LOCALE_SETTINGS
import com.example.glucofit.R
import com.example.glucofit.databinding.ActivityUserBinding
import com.example.glucofit.model.User
import com.example.glucofit.presentation.changepassword.ChangePasswordActivity
import com.example.glucofit.presentation.main.MainActivity
import com.example.glucofit.utils.showDialogError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.jetbrains.anko.startActivity

class UserActivity : AppCompatActivity() {

    private lateinit var userBinding: ActivityUserBinding
    private lateinit var userDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private val listenerUser = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.getValue(User::class.java)
            user?.let {
                userBinding.tvEmailUser.text = it.emailUser // Ganti dengan email atau data pengguna yang sesuai dari Firebase
            }
            hideLoading() // Hide loading when data is successfully retrieved
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            showDialogError(this@UserActivity, error.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userBinding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(userBinding.root)

        // Init Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
        currentUser = FirebaseAuth.getInstance().currentUser

        // Redirect to MainActivity if user is not logged in
        if (currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Set email pengguna yang sedang login
        userBinding.tvEmailUser.text = currentUser?.email
        getDataFirebase()

        onAction()
    }

    private fun onAction() {
        userBinding.apply {
            btnCloseUser.setOnClickListener { finish() }

            btnChangeLanguageUser.setOnClickListener {
                startActivity(Intent(ACTION_LOCALE_SETTINGS))
            }

            btnChangePasswordUser.setOnClickListener {
                startActivity<ChangePasswordActivity>()
            }

            btnLogoutUser.setOnClickListener {
                // Sign out from Firebase Auth
                FirebaseAuth.getInstance().signOut()
                // Clear shared preferences
                val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                // Start MainActivity
                startActivity(Intent(this@UserActivity, MainActivity::class.java))
                // Close all activities
                finishAffinity()
            }

            swipeUser.setOnRefreshListener {
                // Show loading and fetch data from Firebase
                showLoading()
                getDataFirebase()
            }
        }
    }

    private fun getDataFirebase() {
        userDatabase.child(currentUser?.uid.toString()).addValueEventListener(listenerUser)
    }

    private fun showLoading() {
        userBinding.swipeUser.isRefreshing = true
    }

    private fun hideLoading() {
        userBinding.swipeUser.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to avoid leaks and unwanted callbacks
        userDatabase.removeEventListener(listenerUser)
    }
}

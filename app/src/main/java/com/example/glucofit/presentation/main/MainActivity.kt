package com.example.glucofit.presentation.main

import android.content.Intent
import android.os.Bundle
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.glucofit.R
import com.example.glucofit.adapter.ArticleAdapter
import com.example.glucofit.databinding.ActivityMainBinding
import com.example.glucofit.model.Article
import com.example.glucofit.model.User
import com.example.glucofit.presentation.detail.DetailActivity
import com.example.glucofit.utils.showDialogError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.synnapps.carouselview.ImageListener
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import com.example.glucofit.presentation.cta.CtaActivity
import com.example.glucofit.presentation.cek.CekActivity
import com.example.glucofit.presentation.kalender.KalenderActivity
import com.example.glucofit.presentation.user.UserActivity
import java.net.URLEncoder

class MainActivity : AppCompatActivity(), ArticleAdapter.OnItemClickListener {

    // Carousel images
    private val sampleImages = intArrayOf(
        R.drawable.carousel1,
        R.drawable.carousel2,
        R.drawable.carousel3
    )

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var userDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private val listenerUser = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.getValue(User::class.java)
            user?.let {
                mainBinding.tvHalo.text = it.emailUser // Ganti dengan email atau data pengguna yang sesuai dari Firebase
            }
        }

        override fun onCancelled(error: DatabaseError) {
            showDialogError(this@MainActivity, error.message)
        }
    }

    // Recyclerview
    private val article: MutableList<Article> = ArrayList()
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        // Check if user is logged in
        currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            mainBinding.tvHalo.text = "Halo Pengguna" // Set default text
        } else {
            mainBinding.tvHalo.text = currentUser?.email ?: "" // Set email user yang sudah login
        }

        // Init Firebase
        userDatabase = FirebaseDatabase.getInstance().getReference("users")

        // Setup CarouselView
        mainBinding.carouselView.setPageCount(sampleImages.size)
        mainBinding.carouselView.setImageListener(imageListener)

        // Fetch data from Firebase
        currentUser?.let {
            getDataFirebase()
        }

        // Load data from JSON
        getListArticle()
        onAction(currentUser != null)
    }

    private val imageListener: ImageListener = ImageListener { position, imageView ->
        Glide.with(this@MainActivity)
            .load(sampleImages[position])
            .transform(CenterCrop(), RoundedCorners(32))
            .into(imageView)
    }

    private fun getDataFirebase() {
        userDatabase.child(currentUser?.uid.toString()).addValueEventListener(listenerUser)
    }

    private fun onAction(isLoggedIn: Boolean) {
        mainBinding.apply {

            tvHalo.setOnClickListener {
                if (isLoggedIn) {
                    startActivity(Intent(this@MainActivity, UserActivity::class.java))
                }
            }

            ivMenu1.setOnClickListener {
                if (isLoggedIn) {
                    startActivity(Intent(this@MainActivity, CekActivity::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, CtaActivity::class.java))
                }
            }

            ivMenu2.setOnClickListener {
                if (isLoggedIn) {
                    startActivity(Intent(this@MainActivity, KalenderActivity::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, CtaActivity::class.java))
                }
            }


            ivMenu3.setOnClickListener {
                if (isLoggedIn) {
                    sendWhatsAppMessage()
                } else {
                    startActivity(Intent(this@MainActivity, CtaActivity::class.java))
                }
            }
        }
    }

    private fun sendWhatsAppMessage() {
        val phoneNumber = "+6283184450382" // Nomor WhatsApp tujuan
        val message = "Hai, saya ingin konsultasi lebih lanjut mengenai diabetes. Apakah saya bisa konsultasi dengan Anda sekarang?"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${URLEncoder.encode(message, "UTF-8")}")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle jika WhatsApp tidak terinstall atau URI tidak valid
            showDialogError(this@MainActivity, "WhatsApp tidak terpasang atau terjadi kesalahan")
        }
    }

    private fun getListArticle() {
        try {
            val stream = assets.open("article.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArray = jsonObject.getJSONArray("list_article")
                for (i in 0 until jsonArray.length()) {
                    val jsonObjectData = jsonArray.getJSONObject(i)
                    val dataApi = Article().apply {
                        judul = jsonObjectData.getString("judul")
                        penjelasan = jsonObjectData.getString("penjelasan")
                        img = jsonObjectData.getString("img")
                        yt = jsonObjectData.getString("yt") // tambahkan ini jika ingin menambahkan link YouTube
                    }
                    article.add(dataApi)
                }
                articleAdapter = ArticleAdapter(this, article, this@MainActivity)
                val manager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                mainBinding.rvAcne.layoutManager = manager
                mainBinding.rvAcne.adapter = articleAdapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(article: Article) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ARTICLE, article)
        }
        startActivity(intent)
    }
}

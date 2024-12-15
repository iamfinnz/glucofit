package com.example.glucofit.presentation.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.glucofit.databinding.ActivityDetailBinding
import com.example.glucofit.model.Article
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var article: Article
    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data artikel dari intent
        article = intent.getParcelableExtra(EXTRA_ARTICLE)
            ?: throw IllegalArgumentException("Article must not be null")

        // Setup tampilan judul dan penjelasan artikel
        binding.tvDetailTitle.text = article.judul
        binding.tvDetailDescription.text = article.penjelasan

        // Setup YouTube Player
        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = getYouTubeVideoId(article.yt)
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })

        onAction()
    }

    private fun onAction() {
        binding.btnCloseDetail.setOnClickListener { finish() }
    }

    private fun getYouTubeVideoId(youtubeUrl: String): String {
        var videoId = ""
        if (youtubeUrl.isNotEmpty()) {
            val split = youtubeUrl.split("v=")
            if (split.size > 1) {
                videoId = split[1]
            }
        }
        return videoId
    }

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }
}

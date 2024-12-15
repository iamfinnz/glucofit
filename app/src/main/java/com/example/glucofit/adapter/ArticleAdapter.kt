package com.example.glucofit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glucofit.databinding.ItemArticleBinding
import com.example.glucofit.model.Article

class ArticleAdapter(
    private val context: Context,
    private val articles: List<Article>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                tvJudulArticle.text = article.judul
                Glide.with(context)
                    .load(article.img)
                    .into(imgArticle)

                // Handle item click
                root.setOnClickListener {
                    listener.onItemClick(article)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}

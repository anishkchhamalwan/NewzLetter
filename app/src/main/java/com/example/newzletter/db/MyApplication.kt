package com.example.newzletter.db

import android.app.Application

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Room database
        val database = ArticleDatabase(this)

        // Access ArticleDao
        val articleDao = database.getArticleDao()

        // You can use the articleDao to perform database operations
        // For example: val articles = articleDao.getAllArticles()
    }
}
package com.example.newzletter.models

import com.example.newzletter.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
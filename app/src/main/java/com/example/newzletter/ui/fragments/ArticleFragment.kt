package com.example.newzletter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.newzletter.R
import com.example.newzletter.databinding.FragmentArticleBinding
import com.example.newzletter.databinding.FragmentSavedNewsBinding
import com.example.newzletter.db.ArticleDatabase
import com.example.newzletter.repository.NewsRepository
import com.example.newzletter.ui.NewsActivity
import com.example.newzletter.ui.NewsViewModel
import com.example.newzletter.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar

class ArticleFragment: Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()
    private var binding: FragmentArticleBinding? = null
    private var webView:WebView?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(layoutInflater)
        webView=binding?.webView

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireActivity()))
        val viewModelProviderFactory=  NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        val article=args.article
        webView?.apply{
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        binding?.fab?.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Saved Article Successfully",Snackbar.LENGTH_LONG).show()
        }
    }
}
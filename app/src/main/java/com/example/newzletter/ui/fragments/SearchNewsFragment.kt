package com.example.newzletter.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzletter.R
import com.example.newzletter.adapters.NewsAdapter
import com.example.newzletter.databinding.FragmentBreakingNewsBinding
import com.example.newzletter.databinding.FragmentSearchNewsBinding
import com.example.newzletter.db.ArticleDatabase
import com.example.newzletter.repository.NewsRepository
import com.example.newzletter.ui.NewsActivity
import com.example.newzletter.ui.NewsViewModel
import com.example.newzletter.ui.NewsViewModelProviderFactory
import com.example.newzletter.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    val TAG="SearchNewsFragmet"

    lateinit var newsAdapter:NewsAdapter
    private var binding: FragmentSearchNewsBinding?=null
    private var rvB : RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(layoutInflater)
        rvB = binding?.rvSearchNews

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = NewsRepository(ArticleDatabase(requireActivity()))
        val viewModelProviderFactory=  NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        // viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job:Job?=null
        binding!!.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch {
                delay(500L)
                editable?.let{
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let{newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                    }
                }
                is Resource.Error-> {
                    hideProgressBar()
                    response.message.let{ message->
                        Log.e(TAG,"A error occured$message")
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }

                else -> {}
            }
        })
    }
    private fun hideProgressBar(){
        binding!!.paginationProgressBar.visibility=View.INVISIBLE
    }

    private fun showProgressBar(){
        binding!!.paginationProgressBar.visibility=View.VISIBLE
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvB!!.apply{
            adapter=newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
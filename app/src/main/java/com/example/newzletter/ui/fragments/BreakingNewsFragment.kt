package com.example.newzletter.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzletter.R
import com.example.newzletter.adapters.NewsAdapter
import com.example.newzletter.databinding.FragmentBreakingNewsBinding
import com.example.newzletter.db.ArticleDatabase
import com.example.newzletter.repository.NewsRepository
import com.example.newzletter.ui.NewsViewModel
import com.example.newzletter.ui.NewsViewModelProviderFactory
import com.example.newzletter.util.Resource

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter:NewsAdapter
    private var binding:FragmentBreakingNewsBinding?=null
    private var rvB :RecyclerView?=null
    val TAG = "BreakingNewsFragment"

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        rvB = view?.findViewById(R.id.rvBreakingNews)
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater)

        return binding?.root
    }

     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater)
        rvB = binding?.rvBreakingNews

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
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response->
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
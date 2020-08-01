package com.example.mviarchitecture.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mviarchitecture.R
import com.example.mviarchitecture.model.BlogPost
import com.example.mviarchitecture.ui.DataStateListener
import com.example.mviarchitecture.ui.main.state.MainStateEvent.*
import com.example.mviarchitecture.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.ClassCastException
import java.lang.Exception

class MainFragment : Fragment(), BlogRecyclerAdapter.Interaction {

    lateinit var viewModel: MainViewModel

    lateinit var dataStateHandler: DataStateListener

    lateinit var recyclerAdapter: BlogRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        subscribeObservers()
        initRecyclerView()
    }

    fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = BlogRecyclerAdapter(this@MainFragment)
            adapter = recyclerAdapter
        }
    }

    fun subscribeObservers() {
        viewModel.dataState.removeObservers(viewLifecycleOwner)
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            println("DEBUG: DataState: ${dataState}")

            // Handles Loading and Error Message
            dataStateHandler.onDataStateChange(dataState)

            // Handle Data
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let {
                    it.blogPost?.let { blogPosts ->
                        viewModel.setBlogListData(blogPosts)
                    }

                    it.user?.let { user ->
                        viewModel.setUser(user)
                    }
                }
            }
        })

        viewModel.viewState.removeObservers(viewLifecycleOwner)
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            viewState.blogPost?.let {
                recyclerAdapter.submitList(it)
            }

            viewState.user?.let {
                email.text = it.email
                username.text = it.username

                Glide.with(requireContext())
                    .load(it.image)
                    .into(image)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_get_user -> triggerGetUserEvent()

            R.id.action_get_blogs -> triggerGetBlogsEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun triggerGetBlogsEvent() {
        viewModel.setStateEvent(GetBlogPostsEvent())
    }

    private fun triggerGetUserEvent() {
        viewModel.setStateEvent(GetUserEvent("1"))
    }

    // The exception will be executed when the activity the fragment is attached to does not implement and override the interface's function
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must implement DataStateListener")
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        showToast("POSITION = $position : ITEM = $item")
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
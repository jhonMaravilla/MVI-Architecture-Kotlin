package com.example.mviarchitecture.ui.main

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mviarchitecture.R
import com.example.mviarchitecture.ui.DataStateListener
import com.example.mviarchitecture.util.DataState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DataStateListener {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        showMainFragment()
    }

    fun showMainFragment(){
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            MainFragment(), "MainFragment")
            .commit()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let{
            // Handle loading
            showProgressBar(it.loading)

            // Handle message
            it.message?.let {event ->
                event.getContentIfNotHandled()?.let{
                    showToast(it)
                }
            }

        }
    }


    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showProgressBar(isVisible: Boolean){
        if (isVisible){
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }
}
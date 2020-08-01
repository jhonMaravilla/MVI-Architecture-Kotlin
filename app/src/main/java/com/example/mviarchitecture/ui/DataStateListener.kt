package com.example.mviarchitecture.ui

import com.example.mviarchitecture.util.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)
}
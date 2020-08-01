package com.example.mviarchitecture.ui.main.state

import com.example.mviarchitecture.model.BlogPost
import com.example.mviarchitecture.model.User

data class MainViewState(
    var blogPost: List<BlogPost>? = null,
    var user: User? = null
) {
}
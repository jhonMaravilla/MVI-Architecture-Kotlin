package com.example.mviarchitecture.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.mviarchitecture.model.BlogPost
import com.example.mviarchitecture.model.User
import com.example.mviarchitecture.repository.Repository
import com.example.mviarchitecture.ui.main.state.MainStateEvent
import com.example.mviarchitecture.ui.main.state.MainStateEvent.*
import com.example.mviarchitecture.ui.main.state.MainViewState
import com.example.mviarchitecture.util.AbsentLiveData
import com.example.mviarchitecture.util.DataState


class MainViewModel : ViewModel() {

    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    val viewState: LiveData<MainViewState>
        get() = _viewState

    val dataState: LiveData<DataState<MainViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->

            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        when (stateEvent) {
            is GetBlogPostsEvent -> {
                //return AbsentLiveData.create()
                return Repository.getBlogPosts()
            }

            is GetUserEvent -> {
                //return AbsentLiveData.create()
                return Repository.getUser(stateEvent.userID)
            }

            is None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setBlogListData(blogPosts: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogPost = blogPosts
        _viewState.value = update
    }

    fun setUser(user: User) {
        val update = getCurrentViewStateOrNew()
        update.user = user
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): MainViewState {
        val value = viewState.value?.let {
            it
        } ?: MainViewState()

        return value
    }

    fun setStateEvent(event: MainStateEvent) {
        _stateEvent.value = event
    }

}
package com.codepunk.codepunk.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.codepunk.codepunk.data.model.User
import com.codepunk.codepunk.data.model.UserState
import com.codepunk.codepunk.data.model.UserState.Loading

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    var user: LiveData<UserState> = MutableLiveData<UserState>().apply {
        value = UserState.Pending
    }

    fun init() {
        // See https://developer.android.com/jetpack/docs/guide
        // Similar except user doesn't start out as null
        when (user.value) {
            is Loading, is User -> {
                // ViewModel is created per Fragment so
                // we know the userId won't change
                return
            }
            else -> {
                //user = userRepository.getUser()
            }
        }
    }
}
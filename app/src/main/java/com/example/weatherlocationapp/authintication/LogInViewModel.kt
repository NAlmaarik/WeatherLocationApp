package com.example.weatherlocationapp.authintication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LogInViewModel : ViewModel() {


    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if(user != null){
            AuthenticationState.AUTHENTICATED
        }else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}
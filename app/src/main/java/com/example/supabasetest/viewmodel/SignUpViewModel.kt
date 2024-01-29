package com.example.supabasetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabasetest.utils.Constants
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class SignUpViewModel(
) : ViewModel() {

    fun onSignUpEmail(emailUser: String, passwordUser:String) {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Email) {
                email = emailUser
                password = passwordUser
            }
        }
    }
    fun onSignInEmailCode(emailUser: String) {
        viewModelScope.launch {
            Constants.supabase.auth.signInWith(OTP) {
                email = emailUser
                createUser = false
            }
        }
    }
    fun verifyEmailCode(emailUser: String,code:String) {
        viewModelScope.launch {
            Constants.supabase.auth.verifyEmailOtp(
                type = OtpType.Email.MAGIC_LINK,
                email = emailUser,
                token = code)
        }
        println("Done")
    }
    fun onSignUpGoogle() {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Google)
        }
    }
}
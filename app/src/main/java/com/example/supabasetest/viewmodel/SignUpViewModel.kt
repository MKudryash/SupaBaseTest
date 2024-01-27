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

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _password = MutableStateFlow("")
    val password = _password

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignUpEmail() {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Email) {
                email = "jukudryash@gmail.com"
                password = "_password.value"

            }
        }
    }
    fun onSignInEmailCode() {
        viewModelScope.launch {
            Constants.supabase.auth.signInWith(OTP) {
                email = "jukudryash@gmail.com"
                createUser = false
            }
        }
    }
    fun verifyEmailCode() {
        viewModelScope.launch {
            Constants.supabase.auth.verifyEmailOtp(type = OtpType.Email.INVITE, email = "jukudryash@gmail.com", token = "467914")
        }
    }
    fun onSignUpGoogle() {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Google)
        }
    }
}
package com.example.supabasetest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabasetest.utils.Constants
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import kotlinx.coroutines.launch


class SignUpViewModel(
) : ViewModel() {


    fun onSignUpEmail(emailUser: String, passwordUser: String) {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Email) {
                email = emailUser
                password = passwordUser
            }
        }
    }

    fun onSignInEmailCode(emailUser: String) {
        viewModelScope.launch {
            try {
                Constants.supabase.auth.signInWith(OTP) {
                    email = emailUser
                    createUser = false
                }

            } catch (e: Exception) {
                println(e.message.toString())

            }

        }
    }

    fun onSignInEmailPassword(emailUser: String, passwordUser: String) {
        viewModelScope.launch {
            try {
                val user = Constants.supabase.auth.signInWith(Email) {
                    email = emailUser
                    password = passwordUser
                }

                println("Success")
            } catch (e: Exception) {
                println("Error")
                println(e.message.toString())

            }
        }
    }

    fun verifyEmailCode(emailUser: String, code: String) {
        viewModelScope.launch {
            try {
                Constants.supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.MAGIC_LINK,
                    email = emailUser,
                    token = code
                )

                println("Done")

            } catch (e: Exception) {
                println(e.message.toString())

            }
        }
    }

    fun onSignUpGoogle() {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Google)
        }
    }

    fun channel() {

        /*   val channel = Constants.supabase.channel("channelId") {
               //optional config
           }

           val broadcastFlow = channel.broadcastFlow<Message>(event = "message")

   //Collect the flow
           broadcastFlow.collect { //it: Message
               println(it)
           }

           channel.subscribe(blockUntilSubscribed = true)

           channel.broadcast(event = "message", Message("I joined!", "John"))*/
    }

    fun onSignInGoogle() {
        viewModelScope.launch {
            try {
                Constants.supabase.auth.signInWith(
                    provider = Google
                )
            } catch (e: Exception) {
                println(e.message.toString())
            }
        }
    }

    fun stateGoogle() {
        viewModelScope.launch {

            // println(errorMessage)
        }
    }

    fun getUSer() {
        viewModelScope.launch {
            val user =
                Constants.supabase.auth.retrieveUserForCurrentSession(updateSession = true)
        }
    }

    fun resetPassword(emailUser: String) {
        viewModelScope.launch {
            try {
                Log.d("User", Constants.supabase.auth.sessionStatus.value.toString())

                Constants.supabase.auth.resetPasswordForEmail(
                    emailUser
                )


            } catch (e: Exception) {
                println(e.message.toString())

            }
        }
    }


    fun updateUser(emailUser: String, passwordUser: String) {
        viewModelScope.launch {
            try {
                Constants.supabase.auth.modifyUser(true) {
                    email = emailUser
                    password = passwordUser
                }

                println("Success")
            } catch (e: Exception) {
                println("Error")
                println(e.message.toString())

            }
        }
    }

}
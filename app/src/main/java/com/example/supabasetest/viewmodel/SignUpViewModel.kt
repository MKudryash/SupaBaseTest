package com.example.supabasetest.viewmodel


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabasetest.models.Message
import com.example.supabasetest.utils.Constants
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class SignUpViewModel(
) : ViewModel() {
    private val _userState = mutableStateOf("2134123")
    val userState: State<String> = _userState

    fun onSignUpEmail(emailUser: String, passwordUser: String) {
        viewModelScope.launch {
            Constants.supabase.auth.signUpWith(Email) {
                email = emailUser
                password = passwordUser
            }
        }
    }

    fun realTimeDb(scope: CoroutineScope) {
        viewModelScope.launch {
            try {
                val channel = Constants.supabase.channel("test")
                val dataFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public")

                dataFlow.onEach {
                    when (it) {
                        is PostgresAction.Delete -> println("Deleted: ${it.oldRecord}")
                        is PostgresAction.Insert -> println("Inserted: ${it.record}")
                        is PostgresAction.Select -> println("Selected: ${it.record}")
                        is PostgresAction.Update -> {
                            val str = it.record.toString()
                            val data = Json.decodeFromString<Message>(str)
                            /*  Log.d("UPDATE", data.data.toString())*/
                        }
                    }
                }.launchIn(scope)
                channel.subscribe()
            } catch (e: Exception) {

            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            try {

                Constants.supabase.postgrest.from("messages").insert(
                    Message(
                        0,
                        "2024-02-13 13:30:59.626024+00",
                        5,
                        "58575f78-3a0a-4d92-bf8a-645b2aa50118",
                        "text",
                        false,
                        "test"
                    )
                )
            } catch (e: Exception) {
                Log.d("AAAAAAAAAAAAa",e.message.toString())
            }
        }
    }

    fun getNote() {
        viewModelScope.launch {
            try {
                val data = Constants.supabase.postgrest.from("messages")
                    .select().decodeList<Message>()
                /*  data.forEach {
                      Log.d("DATA", it.content)
                  }*/
                Log.d("DATA", data.toString())
                _userState.value = data.toString()
            } catch (e: Exception) {

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
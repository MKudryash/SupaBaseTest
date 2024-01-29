package com.example.supabasetest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supabasetest.ui.theme.SupaBaseTestTheme
import com.example.supabasetest.utils.Constants
import com.example.supabasetest.viewmodel.SignUpViewModel
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

val SignUpViewModel = SignUpViewModel()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SupaBaseTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UI()

                }
            }
        }
    }
}

@Preview
@Composable
fun UI() {
    var email: String by rememberSaveable { mutableStateOf("") }
    var code: String by rememberSaveable { mutableStateOf("") }
    var errorMessage: String = ""
    val context = LocalContext.current
    val authState = Constants.supabase.composeAuth.rememberSignInWithGoogle(
        onResult = {
            when (it) {
                NativeSignInResult.ClosedByUser -> errorMessage = "Closed by user"
                is NativeSignInResult.Error -> errorMessage = it.message
                is NativeSignInResult.NetworkError -> errorMessage = it.message
                NativeSignInResult.Success -> Toast.makeText(context,"123",Toast.LENGTH_SHORT).show()
            }
        },
        fallback = {
            //some custom OAuth flow
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomEmail(search = email,
            "Enter Email / Phone Number",
            onValueChange = { newText -> email = newText })
        Button(
            onClick = { SignUpViewModel.onSignInEmailCode(email) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Send Code (SigIn)")
        }
        Button(
            onClick = { SignUpViewModel.onSignUpGoogle() },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "SignUp With Google")
        }
        Button(
            onClick = {
                //SignUpViewModel.onSignInGoogle()
                authState.startFlow()
                if(errorMessage=="Success!") Toast.makeText(context,"123",Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "SignIn With Google")
        }
        CustomEmail(search = code, "Enter Code",
            onValueChange = { newText -> code = newText })
        Button(
            onClick = { SignUpViewModel.verifyEmailCode(email, code) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Verify Email Code")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomEmail(
    search: String,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0XFFE8EFFF))

    ) {
        TextField(
            value = search,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0XFFE8EFFF),
                focusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            ),
            modifier = Modifier.background(Color(0XFFF5F5F9)),
            placeholder = {
                Text(
                    text = hint,
                    color = Color(0XFF578FFF)
                )
            }
        )

    }
}


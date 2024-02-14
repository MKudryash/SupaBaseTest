package com.example.supabasetest

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.app.ActivityCompat
import com.example.supabasetest.ui.theme.SupaBaseTestTheme
import com.example.supabasetest.utils.Constants
import com.example.supabasetest.viewmodel.SignUpViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.gotrue.handleDeeplinks
import java.util.Locale
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SupaBaseTestTheme {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    23
                )
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

// Методы и переменные для получения геолокации (некоторые перемнные объявляются в UI)
// (не забывай про разрешение к геолокации в телефоне)
private var locationManager: LocationManager? = null
lateinit var geocoder: Geocoder
private val locationListener: LocationListener = object : LocationListener {

    override fun onLocationChanged(location: Location) {
        Log.d("Location", "${location.longitude} ${location.latitude}")
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        Log.d("Address", address?.get(0)?.getAddressLine(0).toString())
        Log.d("Address", address!![0].locality)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}

fun location(context: Context) {
    locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?

    try {
        // Request location updates
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    } catch (ex: SecurityException) {
        Log.d("myTag", "Security Exception, no location available")
    }
}

//Методы и переменные для получения наклона
lateinit var sManager: SensorManager
lateinit var sListener: SensorEventListener
lateinit var sensor: Sensor
lateinit var sensor2: Sensor

fun stop(context: Context) {
    sManager.unregisterListener(sListener)

    TimeUnit.SECONDS.sleep(1)
    start(context)
}

fun start(context: Context) {
    val magnetic = FloatArray(9)
    val gravity = FloatArray(9)

    var accrs = FloatArray(3)
    var magf = FloatArray(3)
    val values = FloatArray(3)


    sManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
    sensor2 = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!
    sListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            when (event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> accrs = event.values.clone()
                Sensor.TYPE_MAGNETIC_FIELD -> magf = event.values.clone()
            }

            SensorManager.getRotationMatrix(gravity, magnetic, accrs, magf)

            val outGravity = FloatArray(9)
            SensorManager.remapCoordinateSystem(
                gravity,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                outGravity
            )
            SensorManager.getOrientation(outGravity, values)
            val degree = values[2] * 57.2958f
            Log.d("Angle", degree.toString())
            if (degree > 50) {
                Log.d("Angle", "Star + 1")
                stop(context)

            }
            if (degree < -50) {
                Log.d("Angle", "Star - 1")
                stop(context)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }
    sManager.registerListener(sListener, sensor, SensorManager.SENSOR_DELAY_UI)
    sManager.registerListener(sListener, sensor2, SensorManager.SENSOR_DELAY_UI)
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun UI() {
    val SignUpViewModel = SignUpViewModel()

    LaunchedEffect(Unit) {
        SignUpViewModel.realTimeDb(this)
    }
    val userState by SignUpViewModel.userState
    SignUpViewModel.getNote()
    Text(text = userState.toString())

    geocoder = Geocoder(LocalContext.current, Locale.getDefault())

    var context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(android.Manifest.permission.LOCATION_HARDWARE)
    val cameraPermissionState1 =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPermissionState2 =
        rememberPermissionState(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    //start(context)
    location(context)

    Constants.supabase.handleDeeplinks(Intent(LocalContext.current, MainActivity::class.java))
    var email: String by rememberSaveable { mutableStateOf("") }
    var password: String by rememberSaveable { mutableStateOf("") }
    var newPassword: String by rememberSaveable { mutableStateOf("") }
    var code: String by rememberSaveable { mutableStateOf("") }
    var errorMessage: String = ""
    val authState = Constants.supabase.composeAuth.rememberSignInWithGoogle(
        onResult = {
            when (it) {
                NativeSignInResult.ClosedByUser -> errorMessage = "Closed by user"
                is NativeSignInResult.Error -> errorMessage = it.message
                is NativeSignInResult.NetworkError -> errorMessage = it.message
                NativeSignInResult.Success -> Toast.makeText(context, "123", Toast.LENGTH_SHORT)
                    .show()
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
        CustomEmail(search = password,
            "Password",
            onValueChange = { newText -> password = newText })
        Button(
            onClick = {

                SignUpViewModel.onSignInEmailPassword(email, password)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "SigIn with Email")
        }
        CustomEmail(search = code,
            "Code",
            onValueChange = { newText -> code = newText })
        Button(
            onClick = {
                SignUpViewModel.onSignInEmailCode(email)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Send code")
        }
        Button(
            onClick = {
                SignUpViewModel.verifyEmailCode(email, code)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Verifity OTP")
        }
        /*Button(
            onClick = { SignUpViewModel.onSignUpGoogle() },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "SignUp With Google")
        }
        Button(
            onClick = {
                //SignUpViewModel.onSignInGoogle()
                authState.startFlow()
                if (errorMessage == "Success!") Toast.makeText(context, "123", Toast.LENGTH_SHORT)
                    .show()
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
        }*/
        CustomEmail(search = newPassword,
            "NewPassword",
            onValueChange = { newText -> newPassword = newText })
        Button(
            onClick = {
                SignUpViewModel.resetPassword(email)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Reset Password")
        }
        Button(
            onClick = {
                SignUpViewModel.updateUser(email, newPassword)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Update Password")
        }
        Button(
            onClick = {
                SignUpViewModel.sendMessage()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "SignInGoogle")
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


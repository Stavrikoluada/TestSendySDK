package com.example.testsendysdk.presentation

import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testsendysdk.presentation.screens.LoginScreen
import com.example.testsendysdk.presentation.screens.SmsCodeScreen
import com.example.testsendysdk.presentation.screens.SplashScreen
import com.example.testsendysdk.ui.theme.TestSendySDKTheme
import land.sendy.pfe_sdk.activies.MasterActivity
import land.sendy.pfe_sdk.api.API

class MainActivity : MasterActivity() {

    private val SERVER_URL = "https://testwallet.sendy.land/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = API.getInsatce(SERVER_URL, "sendy")
        lp = Looper.getMainLooper()

        enableEdgeToEdge()
        setContent {
            TestSendySDKTheme {
                App(api)
            }
        }
    }
}

@Composable
fun App(api: API) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToSmsCode = {
                    navController.navigate("sms_code")
                },
                api = api
            )
        }
        composable("sms_code") {
            SmsCodeScreen(
                onSuccess = {
                    // После успешной активации кошелька
                    if (api.isActivated(navController.context)) {
                        // Переходим к основному функционалу
                    }
                }
            )
        }
    }
} 
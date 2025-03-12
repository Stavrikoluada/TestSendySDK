package com.example.testsendysdk.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testsendysdk.screens.LoginScreen
import com.example.testsendysdk.screens.SmsCodeScreen
import com.example.testsendysdk.screens.SplashScreen
import com.example.testsendysdk.screens.SuccessScreen
import land.sendy.pfe_sdk.api.API

private const val SPLASH_KEY = "splash"
private const val LOGIN_KEY = "login"
private const val SMS_KODE_KEY = "sms_code"
private const val SUCCESS_KEY = "success"

@Composable
fun AppNavigation(api: API) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SPLASH_KEY) {
        composable(SPLASH_KEY) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(LOGIN_KEY) {
                        popUpTo(SPLASH_KEY) { inclusive = true }
                    }
                }
            )
        }
        composable(LOGIN_KEY) {
            LoginScreen(
                onNavigateToSmsCode = {
                    navController.navigate(SMS_KODE_KEY)
                },
                api = api
            )
        }
        composable(SMS_KODE_KEY) {
            SmsCodeScreen(
                onSuccess = {
                    navController.navigate(SUCCESS_KEY)
                }
            )
        }
        composable(SUCCESS_KEY) {
            SuccessScreen()
        }
    }
}
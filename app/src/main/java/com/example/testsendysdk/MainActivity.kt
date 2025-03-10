package com.example.testsendysdk

import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.testsendysdk.navigation.AppNavigation
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
                AppNavigation(api)
            }
        }
    }
}
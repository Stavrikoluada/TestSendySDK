package com.example.testsendysdk.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.types.ApiCallback

class SmsCodeViewModel : ViewModel() {
    private val api = API.getInstance()

    fun isValidSmsCode(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }

    fun validateSmsCode(context: Context, code: String, onResult: (Boolean) -> Unit) {
        try {
            api.activateWlletWS(
                context,
                code,
                "sms",
                object : ApiCallback() {
                    override fun onCompleted(res: Boolean) {
                        Log.d("SmsCodeViewModel", "API response: $res")
                        onResult(res)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("SmsCodeViewModel", "Error validating code", e)
            onResult(false)
        }
    }
} 
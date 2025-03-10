package com.example.testsendysdk.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.pfe.response.AuthActivateRs
import land.sendy.pfe_sdk.model.pfe.response.BResponse
import land.sendy.pfe_sdk.model.types.ApiCallback
import land.sendy.pfe_sdk.model.types.LoaderError

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
                    override fun <T : BResponse?> onSuccess(data: T) {

                        if (data != null) {
                            val response = data as? AuthActivateRs
                            if (response != null) {
                                if (response.Errno == 0) {
                                    onResult(true)
                                } else {
                                    onResult(false)
                                }
                            } else {
                                onResult(false)
                            }
                        } else {
                            onResult(false)
                        }
                    }

                    override fun onFail(error: LoaderError) {
                        onResult(false)
                    }
                }
            )
        } catch (e: Exception) {
            onResult(false)
        }
    }
}
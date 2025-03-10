package com.example.testsendysdk.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.pfe.response.AuthActivateRs
import land.sendy.pfe_sdk.model.pfe.response.AuthLoginRs
import land.sendy.pfe_sdk.model.types.ApiCallback
import java.security.KeyFactory

class LoginViewModel(private val api: API) : ViewModel() {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = Regex("^\\+7\\s?\\d{3}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}\$")
        return phoneRegex.matches(phone.replace(" ", ""))
    }

    fun register(context: Context, phone: String, onResult: (Result) -> Unit) {
        try {
            setupCrypto()

            val cleanPhone = phone.replace(" ", "")
            Log.d("LoginViewModel", "Attempting to register with phone: $cleanPhone")
            
            api.loginAtAuthWS(
                context,
                cleanPhone,
                object : ApiCallback() {
                    override fun onCompleted(res: Boolean) {
                        if (!res || errNo != 0) {
                            Log.e("LoginViewModel", "Registration error: $error")
                            mainHandler.post {
                                onResult(Result.Error(error ?: "Неизвестная ошибка"))
                            }
                            return
                        }

                        val response = oResponse as? AuthLoginRs
                        if (response == null) {
                            mainHandler.post {
                                onResult(Result.Error("Неверный формат ответа"))
                            }
                            return
                        }

                        Log.d("LoginViewModel", "Registration completed successfully")
                        mainHandler.post {
                            onResult(Result.Success(response))
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Registration failed", e)
            mainHandler.post {
                onResult(Result.Error(e.message ?: "Неизвестная ошибка"))
            }
        }
    }

    private fun setupCrypto() {
        try {
            // Инициализируем KeyFactory для RSA используя встроенный провайдер
            KeyFactory.getInstance("RSA")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Failed to initialize crypto", e)
        }
    }

    sealed class Result {
        data class Success(val data: Any) : Result()
        data class Error(val message: String) : Result()
        data class TwoFactorRequired(val email: String) : Result()
    }
} 
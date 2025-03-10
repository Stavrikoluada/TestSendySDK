package com.example.testsendysdk.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.pfe.response.AuthLoginRs
import land.sendy.pfe_sdk.model.pfe.response.TermsOfUseRs
import land.sendy.pfe_sdk.model.types.ApiCallback
import java.security.KeyFactory

class LoginViewModel(private val api: API) : ViewModel() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val _offerText = MutableLiveData<String>()
    val offerText: LiveData<String> get() = _offerText

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
            KeyFactory.getInstance("RSA")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Failed to initialize crypto", e)
        }
    }

    fun getOfferText(context: Context) {
        viewModelScope.launch {
            try {
                api.getTermsOfUseWS(context, object : ApiCallback() {
                    override fun onCompleted(res: Boolean) {
                        API.outLog("Запрос завершен. Результат: $res, Error: ${getErrNo()}")

                        if (!res || getErrNo() != 0) {
                            API.outLog("Ошибка при получении текста соглашения. Код ошибки: ${getErrNo()}")
                            _offerText.postValue("")
                        } else {
                            val jsonResponse = oResponse.toString()
                            API.outLog("Ответ от сервера: $jsonResponse")

                            val termsResponse = oResponse as? TermsOfUseRs

                            termsResponse?.let {
                                API.outLog("Текст соглашения:\r\n${it.TextTermsOfUse}")
                                _offerText.postValue(it.TextTermsOfUse)
                            } ?: run {
                                API.outLog("Ответ невалиден или не содержит текст соглашения.")
                                _offerText.postValue("")
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                _offerText.postValue("")
            }
        }
    }

    sealed class Result {
        data class Success(val data: Any) : Result()
        data class Error(val message: String) : Result()
        data class TwoFactorRequired(val email: String) : Result()
    }
} 
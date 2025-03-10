package com.example.testsendysdk.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testsendysdk.presentation.viewmodel.LoginViewModel
import com.example.testsendysdk.presentation.viewmodel.LoginViewModelFactory
import land.sendy.pfe_sdk.api.API

@Composable
fun LoginScreen(
    onNavigateToSmsCode: () -> Unit,
    api: API
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(api)
    )
    
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Телефон") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.isValidPhoneNumber(phone)) {
                    isLoading = true
                    viewModel.register(context, phone) { result ->
                        isLoading = false
                        when (result) {
                            is LoginViewModel.Result.Success -> {
                                onNavigateToSmsCode()
                            }
                            is LoginViewModel.Result.Error -> {
                                Toast.makeText(
                                    context,
                                    result.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is LoginViewModel.Result.TwoFactorRequired -> {
                                // Сохраняем email для двухфакторной аутентификации если нужно
                                onNavigateToSmsCode()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Неверный формат номера телефона",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Войти")
            }
        }
    }
} 
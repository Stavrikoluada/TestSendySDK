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
    var phone by remember { mutableStateOf("+7") }
    var isLoading by remember { mutableStateOf(false) }
    var isAgreedToOffer by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = phone,
            onValueChange = { newPhone ->
                if (newPhone.startsWith("+7") || newPhone.isEmpty()) {
                    phone = newPhone
                }
            },
            label = { Text("Телефон") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Checkbox(
                checked = isAgreedToOffer,
                onCheckedChange = { isChecked -> isAgreedToOffer = isChecked }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Я согласен с условиями оферты")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isAgreedToOffer) { // Проверяем, согласен ли пользователь
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
                } else {
                    Toast.makeText(
                        context,
                        "Вы должны согласиться с условиями оферты, чтобы продолжить.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = !isLoading && isAgreedToOffer,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Продолжить")
            }
        }
    }
}
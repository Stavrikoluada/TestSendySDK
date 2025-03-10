package com.example.testsendysdk.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testsendysdk.presentation.viewmodel.SmsCodeViewModel

@Composable
fun SmsCodeScreen(
    onSuccess: () -> Unit,
    viewModel: SmsCodeViewModel = viewModel()
) {
    var smsCode by remember { mutableStateOf(TextFieldValue()) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = smsCode,
            onValueChange = { 
                if (it.text.length <= 6) {
                    smsCode = it
                    isError = it.text.length == 6 && !viewModel.isValidSmsCode(it.text)
                }
            },
            label = { Text("Код из SMS") },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text("Введите корректный код")
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.isValidSmsCode(smsCode.text)) {
                    viewModel.validateSmsCode(context, smsCode.text) { success ->
                        if (success) {
                            onSuccess()
                        }
                    }
                }
            },
            enabled = smsCode.text.length == 6 && !isError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить")
        }
    }
} 
package com.example.testsendysdk.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testsendysdk.R
import com.example.testsendysdk.viewmodels.SmsCodeViewModel

@Composable
fun SmsCodeScreen(
    viewModel: SmsCodeViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    var smsCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
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
            onValueChange = { newValue ->
                val filteredText = newValue.filter { it.isDigit() }
                if (filteredText.length <= 6) {
                    smsCode = filteredText
                    isError = filteredText.length == 6 && !viewModel.isValidSmsCode(filteredText)
                }
            },
            label = { Text(text = stringResource(id = R.string.kod_sms)) },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(text = stringResource(id = R.string.enter_correct_code))
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.isValidSmsCode(smsCode)) {
                    viewModel.validateSmsCode(context, smsCode) { success ->
                        if (success) {
                            onSuccess()
                        } else {
                            errorMessage = "Код не верный"
                        }
                    }
                } else {
                    errorMessage = "Некорректный код"
                }
            },
            enabled = smsCode.length == 6 && !isError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.continues))
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
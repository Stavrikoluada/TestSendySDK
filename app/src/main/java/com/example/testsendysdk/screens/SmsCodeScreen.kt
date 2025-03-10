package com.example.testsendysdk.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testsendysdk.R
import com.example.testsendysdk.viewmodels.SmsCodeViewModel

@Composable
fun SmsCodeScreen(
    viewModel: SmsCodeViewModel = viewModel(),
    onSuccess: () -> Unit
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
                if (viewModel.isValidSmsCode(smsCode.text)) {
                    viewModel.validateSmsCode(context, smsCode.text) { success ->
                        if (success) {
                            onSuccess()
                        } else {
                            Log.d("SmsCodeScreen", "Code validation failed.")
                        }
                    }
                }
            },
            enabled = smsCode.text.length == 6 && !isError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.continues))
        }
    }
}
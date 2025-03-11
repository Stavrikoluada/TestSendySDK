package com.example.testsendysdk.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testsendysdk.R
import com.example.testsendysdk.viewmodels.LoginViewModel
import com.example.testsendysdk.viewmodels.LoginViewModelFactory
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
    val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    val savedPhone = sharedPreferences.getString("phone_number", "+7") ?: "+7"
    var phone by remember { mutableStateOf(TextFieldValue(savedPhone)) }
    var isLoading by remember { mutableStateOf(false) }
    var isAgreedToOffer by remember { mutableStateOf(false) }
    var showOfferDialog by remember { mutableStateOf(false) }
    val offerText by viewModel.offerText.observeAsState("")

    LaunchedEffect(Unit) {
        viewModel.getOfferText(context)
    }

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
                val cursorPosition = newPhone.selection.start
                val filteredPhone = newPhone.text.take(12).filter { it.isDigit() || it == '+' }

                if (filteredPhone.startsWith("+7")) {
                    phone = if (cursorPosition <= 2) {
                        TextFieldValue(filteredPhone, TextRange(filteredPhone.length))
                    } else {
                        TextFieldValue(filteredPhone, TextRange(filteredPhone.length))
                    }
                    sharedPreferences.edit().putString("phone_number", filteredPhone).apply()
                } else {
                    phone = TextFieldValue("+7", TextRange(2))
                }
            },
            label = { Text(text = stringResource(id = R.string.telephone)) },
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
            Text(text = stringResource(id = R.string.agree_to_offer),
            modifier = Modifier.clickable {
                isAgreedToOffer = !isAgreedToOffer
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(id = R.string.condition_offer),
            modifier = Modifier.clickable {
                showOfferDialog = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isAgreedToOffer) {
                    if (viewModel.isValidPhoneNumber(phone.text)) {
                        isLoading = true
                        viewModel.register(context, phone.text) { result ->
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

    if (showOfferDialog) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = { Text(text = stringResource(id = R.string.condition_offer)) },
            text = {
                Text(text = offerText.ifEmpty { "Текст соглашения не доступен." })
            },
            confirmButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }
}
package com.example.triply.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.TriplyApplication
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkSecondary
import com.example.triply.ui.viewmodels.CreateEditTripViewModel
import com.example.triply.ui.viewmodels.CreateEditTripViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTripScreen(
    tripId: Long,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: CreateEditTripViewModel = viewModel(factory = CreateEditTripViewModelFactory(repository))
    val scope = rememberCoroutineScope()
    val uiState = viewModel.tripUiState

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Text(
                        if (tripId == -1L) "New Adventure" else "Edit Trip",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PinkPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Plan your details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PinkPrimary
            )

            FeminineTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateUiState(uiState.copy(title = it)) },
                label = "Trip Title",
                placeholder = "Summer in Paris..."
            )

            FeminineTextField(
                value = uiState.location,
                onValueChange = { viewModel.updateUiState(uiState.copy(location = it)) },
                label = "Location",
                placeholder = "City, Country"
            )

            FeminineTextField(
                value = uiState.budget,
                onValueChange = { viewModel.updateUiState(uiState.copy(budget = it)) },
                label = "Budget ($)",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            FeminineTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateUiState(uiState.copy(description = it)) },
                label = "Description",
                placeholder = "What are you planning to do?",
                singleLine = false,
                minLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveTrip()
                        navigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                enabled = uiState.title.isNotBlank() && uiState.location.isNotBlank()
            ) {
                Text("Save Trip", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FeminineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkPrimary,
                unfocusedBorderColor = PinkSecondary.copy(alpha = 0.5f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

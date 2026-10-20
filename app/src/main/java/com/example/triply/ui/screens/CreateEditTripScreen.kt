package com.example.triply.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.ui.components.FeminineTextField
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.CreateEditTripViewModel
import com.example.triply.ui.viewmodels.CreateEditTripViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDateRangePickerState()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Text(
                        if (tripId == -1L) stringResource(R.string.new_adventure) else stringResource(R.string.edit_trip),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = PinkPrimary)
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
                text = stringResource(R.string.plan_details),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PinkPrimary
            )

            FeminineTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateUiState(uiState.copy(title = it)) },
                label = stringResource(R.string.trip_title),
                placeholder = stringResource(R.string.trip_title_placeholder)
            )

            FeminineTextField(
                value = uiState.location,
                onValueChange = { viewModel.updateUiState(uiState.copy(location = it)) },
                label = stringResource(R.string.location),
                placeholder = stringResource(R.string.location_placeholder)
            )

            // Date Selection UI
            Column {
                Text(
                    text = stringResource(R.string.travel_dates),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                Surface(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = PinkPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${dateFormat.format(Date(uiState.startDate))} — ${dateFormat.format(Date(uiState.endDate))}",
                            color = Color.DarkGray
                        )
                    }
                }
            }

            FeminineTextField(
                value = uiState.budget,
                onValueChange = { viewModel.updateUiState(uiState.copy(budget = it)) },
                label = stringResource(R.string.budget_label),
                placeholder = stringResource(R.string.budget_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            FeminineTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateUiState(uiState.copy(description = it)) },
                label = stringResource(R.string.description),
                placeholder = stringResource(R.string.description_placeholder),
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
                Text(stringResource(R.string.save_trip), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val start = datePickerState.selectedStartDateMillis ?: uiState.startDate
                        val end = datePickerState.selectedEndDateMillis ?: uiState.endDate
                        viewModel.updateUiState(uiState.copy(startDate = start, endDate = end))
                        showDatePicker = false
                    }) { Text(stringResource(R.string.confirm), color = PinkPrimary) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
                }
            ) {
                DateRangePicker(
                    state = datePickerState,
                    modifier = Modifier.weight(1f),
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = PinkPrimary,
                        todayContentColor = PinkPrimary,
                        todayDateBorderColor = PinkPrimary
                    )
                )
            }
        }
    }
}

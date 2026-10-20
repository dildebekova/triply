package com.example.triply.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.data.local.ScheduleItem
import com.example.triply.ui.components.FeminineTextField
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkSecondary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.ScheduleViewModel
import com.example.triply.ui.viewmodels.ScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(repository, tripId))
    val schedule by viewModel.scheduleState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.trip_schedule), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = PinkPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event))
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (schedule.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_events), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(schedule, key = { it.id }) { item ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInHorizontally()
                        ) {
                            ScheduleItemRow(item = item, onDelete = { viewModel.deleteScheduleItem(item) })
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddScheduleDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, dateTime, loc, desc ->
                    viewModel.addScheduleItem(title, dateTime, loc, desc)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ScheduleItemRow(item: ScheduleItem, onDelete: () -> Unit) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
    val dateFormat = SimpleDateFormat("dd MMM", Locale("ru"))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(text = timeFormat.format(Date(item.dateTime)), fontWeight = FontWeight.Bold, color = PinkPrimary, fontSize = 16.sp)
            Text(text = dateFormat.format(Date(item.dateTime)), color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(2.dp).height(40.dp).background(PinkSecondary.copy(alpha = 0.3f)))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = PinkSecondary.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
                Text(text = item.location, style = MaterialTheme.typography.bodySmall, color = PinkPrimary, fontWeight = FontWeight.Medium)
                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AddScheduleDialog(onDismiss: () -> Unit, onConfirm: (String, Long, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val dateTime = System.currentTimeMillis() 

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = { Text(stringResource(R.string.add_event), fontWeight = FontWeight.Bold, color = PinkPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeminineTextField(value = title, onValueChange = { title = it }, label = stringResource(R.string.event_title), placeholder = stringResource(R.string.event_placeholder))
                FeminineTextField(value = location, onValueChange = { location = it }, label = stringResource(R.string.event_location), placeholder = stringResource(R.string.location_placeholder))
                FeminineTextField(value = desc, onValueChange = { desc = it }, label = stringResource(R.string.description), placeholder = stringResource(R.string.note_placeholder_expense), singleLine = false)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, dateTime, location, desc) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = Color.Gray) }
        }
    )
}

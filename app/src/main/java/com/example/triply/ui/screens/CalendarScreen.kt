package com.example.triply.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.data.local.ScheduleItem
import com.example.triply.data.local.Trip
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkSecondary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.CalendarViewModel
import com.example.triply.ui.viewmodels.CalendarViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onTripClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Column {
                        Text(stringResource(R.string.trip_calendar), fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.selectedTripId == null) "Общий план" else "План поездки",
                            style = MaterialTheme.typography.labelSmall,
                            color = PinkPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Быстрый выбор поездки
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedTripId == null,
                        onClick = { viewModel.selectTrip(null) },
                        label = { Text("Все") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(uiState.trips) { trip ->
                    FilterChip(
                        selected = uiState.selectedTripId == trip.id,
                        onClick = { viewModel.selectTrip(trip.id) },
                        label = { Text(trip.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (uiState.totalEvents == 0) {
                EmptyCalendarView(uiState.selectedTripId != null)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.scheduleByDate.forEach { (date, items) ->
                        item {
                            DateHeader(date)
                        }
                        items(items) { event ->
                            val trip = uiState.trips.find { it.id == event.tripId }
                            GlobalScheduleItem(
                                item = event, 
                                tripTitle = trip?.title ?: "",
                                onCardClick = { onTripClick(event.tripId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCalendarView(isFiltered: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = PinkSecondary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isFiltered) "В этой поездке пока нет планов" else "Ваш календарь пуст",
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DateHeader(date: String) {
    val today = SimpleDateFormat("EEEE, dd MMMM", Locale("ru")).format(Date())
    val isToday = date == today

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isToday) PinkPrimary else MaterialTheme.colorScheme.onBackground
        )
        if (isToday) {
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PinkPrimary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("СЕГОДНЯ", color = PinkPrimary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun GlobalScheduleItem(
    item: ScheduleItem, 
    tripTitle: String,
    onCardClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Time bubble
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkTertiary.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeFormat.format(Date(item.dateTime)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            }
            // Линия таймлайна
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(2.dp)
                    .height(40.dp)
                    .background(PinkSecondary.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onCardClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Тэг поездки
                    if (tripTitle.isNotBlank()) {
                        Text(
                            text = tripTitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = PinkPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = item.location,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
                
                Icon(
                    Icons.Default.NavigateNext, 
                    contentDescription = null, 
                    tint = PinkSecondary
                )
            }
        }
    }
}

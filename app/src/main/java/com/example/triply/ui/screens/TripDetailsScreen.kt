package com.example.triply.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.ui.theme.*
import com.example.triply.ui.viewmodels.TripDetailsViewModel
import com.example.triply.ui.viewmodels.TripDetailsViewModelFactory
import com.example.triply.ui.viewmodels.TripDetailsUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    onEditTripClick: (Long) -> Unit,
    onPlacesClick: (Long) -> Unit,
    onScheduleClick: (Long) -> Unit,
    onExpensesClick: (Long) -> Unit,
    onNotesClick: (Long) -> Unit,
    onPhotosClick: (Long) -> Unit,
    onPackingListClick: (Long) -> Unit,
    onDocumentsClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: TripDetailsViewModel = viewModel(
        factory = TripDetailsViewModelFactory(repository, tripId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val trip = uiState.trip

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(trip?.title ?: "", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (trip != null) {
                        val shareText = stringResource(R.string.share_trip_plan)
                        val summaryTitle = stringResource(R.string.share_summary_title, trip.title)
                        val summaryLocation = stringResource(R.string.share_summary_location, trip.location)
                        val scheduleHeader = stringResource(R.string.share_summary_schedule_header)
                        val footer = stringResource(R.string.share_summary_footer)
                        val datesTemplate = stringResource(R.string.share_summary_dates)
                        val scheduleItemTemplate = stringResource(R.string.share_summary_schedule_item)

                        IconButton(onClick = {
                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))
                            val summary = buildString {
                                appendLine(summaryTitle)
                                appendLine(summaryLocation)
                                appendLine(String.format(datesTemplate, dateFormat.format(Date(trip.startDate)), dateFormat.format(Date(trip.endDate))))
                                if (uiState.scheduleItems.isNotEmpty()) {
                                    appendLine(scheduleHeader)
                                    uiState.scheduleItems.forEach { item ->
                                        val time = SimpleDateFormat("HH:mm", Locale("ru")).format(Date(item.dateTime))
                                        appendLine(String.format(scheduleItemTemplate, time, item.title, item.location))
                                    }
                                }
                                appendLine(footer)
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, shareText))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = shareText, tint = PinkPrimary)
                        }
                        IconButton(onClick = { onEditTripClick(trip.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_trip), tint = PinkPrimary)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (trip == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PinkPrimary)
            }
        } else {
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Main Gradient Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PinkPrimary, HotPink)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = trip.location,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${dateFormat.format(Date(trip.startDate))} - ${dateFormat.format(Date(trip.endDate))}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Weather Section
                if (uiState.weather != null || uiState.isWeatherLoading) {
                    SectionTitle(title = stringResource(R.string.local_weather))
                    WeatherCard(uiState)
                }

                // Packing Progress Section
                if (uiState.totalItemsCount > 0) {
                    SectionTitle(title = stringResource(R.string.packing_readiness))
                    PackingProgressCard(uiState)
                }

                // Budget Section
                SectionTitle(title = stringResource(R.string.budget_status))
                BudgetCard(uiState)

                // Menu Grid
                SectionTitle(title = stringResource(R.string.trip_planning))
                
                Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    MenuButton(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.schedule),
                        onClick = { onScheduleClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MenuButton(
                        icon = Icons.Default.Place,
                        label = stringResource(R.string.places),
                        onClick = { onPlacesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    MenuButton(
                        icon = Icons.Default.ShoppingCart,
                        label = stringResource(R.string.expenses),
                        onClick = { onExpensesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MenuButton(
                        icon = Icons.Default.Info,
                        label = stringResource(R.string.notes),
                        onClick = { onNotesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    MenuButton(
                        icon = Icons.Default.CheckCircle,
                        label = stringResource(R.string.packing),
                        onClick = { onPackingListClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MenuButton(
                        icon = Icons.Default.PhotoLibrary,
                        label = stringResource(R.string.photos),
                        onClick = { onPhotosClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                MenuButton(
                    icon = Icons.Default.FolderOpen,
                    label = stringResource(R.string.documents_tickets),
                    onClick = { onDocumentsClick(trip.id) },
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun WeatherCard(uiState: TripDetailsUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isWeatherLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PinkPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(stringResource(R.string.loading_forecast), color = Color.Gray)
            } else {
                Text(
                    text = "${uiState.weather?.temperature ?: 0}°",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = uiState.weather?.condition ?: "Неизвестно", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text(text = stringResource(R.string.current_condition), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun PackingProgressCard(uiState: TripDetailsUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.packed_info, uiState.packedItemsCount, uiState.totalItemsCount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(
                    text = "${(uiState.packingProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PinkPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { uiState.packingProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = PinkPrimary,
                trackColor = PinkTertiary.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
        style = MaterialTheme.typography.titleMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun BudgetCard(uiState: TripDetailsUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(stringResource(R.string.expenses), color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = uiState.currency + String.format(Locale.getDefault(), "%.0f", uiState.totalExpenses),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray)
            )
            Column {
                Text(stringResource(R.string.remaining), color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = uiState.currency + String.format(Locale.getDefault(), "%.0f", uiState.remainingBudget),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (uiState.remainingBudget >= 0) PinkPrimary else Color.Red
                )
            }
        }
    }
}

@Composable
fun MenuButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = PinkTertiary.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PinkPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = DarkGreyText)
        }
    }
}

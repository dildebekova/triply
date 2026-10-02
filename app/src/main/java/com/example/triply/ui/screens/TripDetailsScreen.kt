package com.example.triply.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.TriplyApplication
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.TripDetailsViewModel
import com.example.triply.ui.viewmodels.TripDetailsViewModelFactory
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (trip != null) {
                        IconButton(onClick = { onEditTripClick(trip.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Trip",
                                tint = PinkPrimary
                            )
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
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
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
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${dateFormat.format(Date(trip.startDate))} - ${dateFormat.format(Date(trip.endDate))}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Budget Section with WhatsApp-like clean look
                SectionTitle(title = "Budget Status")
                BudgetCard(uiState)

                // Menu Grid
                SectionTitle(title = "Trip Planning")
                
                Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    MenuButton(
                        icon = Icons.Default.DateRange,
                        label = "Schedule",
                        onClick = { onScheduleClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MenuButton(
                        icon = Icons.Default.Place,
                        label = "Places",
                        onClick = { onPlacesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    MenuButton(
                        icon = Icons.Default.ShoppingCart,
                        label = "Expenses",
                        onClick = { onExpensesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MenuButton(
                        icon = Icons.Default.Info,
                        label = "Notes",
                        onClick = { onNotesClick(trip.id) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                MenuButton(
                    icon = Icons.Default.AccountBox,
                    label = "Photos Gallery",
                    onClick = { onPhotosClick(trip.id) },
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
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
fun BudgetCard(uiState: com.example.triply.ui.viewmodels.TripDetailsUiState) {
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
                Text("Expenses", color = Color.Gray, fontSize = 14.sp)
                Text("$${String.format(Locale.getDefault(), "%.0f", uiState.totalExpenses)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 16.dp))
            Column {
                Text("Remaining", color = Color.Gray, fontSize = 14.sp)
                Text(
                    "$${String.format(Locale.getDefault(), "%.0f", uiState.remainingBudget)}",
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
            Icon(icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = DarkGreyText)
        }
    }
}

// Helper colors not in theme
val HotPink = Color(0xFFF06292)
val DarkGreyText = Color(0xFF4A4A4A)

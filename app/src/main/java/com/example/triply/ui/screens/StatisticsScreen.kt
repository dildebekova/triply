package com.example.triply.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkSecondary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.StatisticsViewModel
import com.example.triply.ui.viewmodels.StatisticsViewModelFactory
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.trip_statistics), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.expenseCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_expenses), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        TotalSummaryCard(uiState.totalSpent, uiState.currency)
                    }

                    item {
                        ChartSection(uiState.categoryBreakdown, uiState.totalSpent)
                    }

                    item {
                        Text(
                            stringResource(R.string.spending_by_category),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }

                    items(uiState.categoryBreakdown.toList()) { (category, amount) ->
                        CategoryProgressRow(category, amount, uiState.totalSpent, uiState.currency)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalSummaryCard(total: Double, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = PinkPrimary)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.total_spent), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(
                text = currency + String.format(Locale.getDefault(), "%.2f", total),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ChartSection(breakdown: Map<String, Double>, total: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        val colors = listOf(PinkPrimary, PinkSecondary, Color(0xFFFF80AB), Color(0xFFFF4081), Color(0xFFF50057))
        
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f
            breakdown.values.forEachIndexed { index, amount ->
                val sweepAngle = (amount.toFloat() / total.toFloat()) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${breakdown.size}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = PinkPrimary)
            Text(stringResource(R.string.category), fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CategoryProgressRow(category: String, amount: Double, total: Double, currency: String) {
    val progress = (amount / total).toFloat()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text(
                text = currency + String.format(Locale.getDefault(), "%.0f", amount),
                fontWeight = FontWeight.Bold,
                color = PinkPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = PinkPrimary,
            trackColor = PinkTertiary.copy(alpha = 0.3f)
        )
    }
}

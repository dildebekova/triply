package com.example.triply.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.ui.components.FeminineTextField
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.ProfileViewModel
import com.example.triply.ui.viewmodels.ProfileViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
            } catch (_: Exception) { }
            viewModel.updateAvatar(it.toString())
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.profile), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val report = viewModel.generateFullReport()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, report)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Экспорт отчета"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export Report", tint = PinkPrimary)
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PinkPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Header / Avatar
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(PinkTertiary)
                    .clickable { 
                        avatarLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.avatarUri != null) {
                    AsyncImage(
                        model = uiState.avatarUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = PinkPrimary
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 4.dp, end = 4.dp)
                        .size(36.dp),
                    shape = CircleShape,
                    color = PinkPrimary,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = uiState.userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = uiState.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = PinkPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = stringResource(R.string.total_trips),
                    value = uiState.totalTrips.toString(),
                    icon = Icons.Default.CardTravel,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(
                    title = stringResource(R.string.countries_visited),
                    value = uiState.totalPlaces.toString(),
                    icon = Icons.Default.Public,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Card
            if (uiState.emergencyContact.isNotBlank() || uiState.medicalNotes.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.Red)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.emergency_info), fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        if (uiState.emergencyContact.isNotBlank()) {
                            Text("📞 ${uiState.emergencyContact}", modifier = Modifier.padding(top = 8.dp))
                        }
                        if (uiState.medicalNotes.isNotBlank()) {
                            Text("🩺 ${uiState.medicalNotes}", modifier = Modifier.padding(top = 4.dp), fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            StatCard(
                title = stringResource(R.string.total_spent_stat),
                value = uiState.currency + String.format(Locale.getDefault(), "%.0f", uiState.totalExpenses),
                icon = Icons.Default.Payments,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Achievement Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val rank = when {
                        uiState.totalTrips >= 10 -> "Легенда"
                        uiState.totalTrips >= 5 -> "Исследователь"
                        uiState.totalTrips >= 1 -> "Путешественник"
                        else -> "Новичок"
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PinkPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ранг: $rank", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (uiState.totalTrips % 5) / 5f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = PinkPrimary,
                        trackColor = PinkTertiary.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Danger Zone
            Text(
                stringResource(R.string.data_management),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp)
            )
            
            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(16.dp),
                elevation = null
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.delete_all_data))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Triply v1.0.4 • 🌸", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showEditDialog) {
            EditProfileDialog(
                currentName = uiState.userName,
                currentBio = uiState.bio,
                currentCurrency = uiState.currency,
                currentDarkMode = uiState.isDarkMode,
                currentEmergency = uiState.emergencyContact,
                currentMedical = uiState.medicalNotes,
                onDismiss = { showEditDialog = false },
                onConfirm = { name, bio, currency, darkMode, emergency, medical ->
                    // Исправлено: используем единый метод saveUserSettings
                    viewModel.saveUserSettings(name, bio, currency, darkMode, emergency, medical)
                    showEditDialog = false
                }
            )
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(stringResource(R.string.delete_confirm_title)) },
                text = { Text(stringResource(R.string.delete_confirm_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllData()
                            showDeleteConfirm = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentBio: String,
    currentCurrency: String,
    currentDarkMode: Boolean?,
    currentEmergency: String,
    currentMedical: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean?, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var bio by remember { mutableStateOf(currentBio) }
    var currency by remember { mutableStateOf(currentCurrency) }
    var darkMode by remember { mutableStateOf(currentDarkMode) }
    var emergency by remember { mutableStateOf(currentEmergency) }
    var medical by remember { mutableStateOf(currentMedical) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold, color = PinkPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeminineTextField(value = name, onValueChange = { name = it }, label = "Имя", placeholder = "Ваше имя")
                FeminineTextField(value = bio, onValueChange = { bio = it }, label = "О себе", placeholder = "Ваш девиз")
                
                HorizontalDivider()
                Text("Экстренная связь", fontWeight = FontWeight.Bold, color = PinkPrimary)
                FeminineTextField(value = emergency, onValueChange = { emergency = it }, label = "Контактный номер", placeholder = "+7...")
                FeminineTextField(value = medical, onValueChange = { medical = it }, label = "Мед. данные", placeholder = "Группа крови, аллергии")

                HorizontalDivider()
                Column {
                    Text("Валюта", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("$", "€", "₽", "₸").forEach { symbol ->
                            FilterChip(
                                selected = currency == symbol,
                                onClick = { currency = symbol },
                                label = { Text(symbol) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Column {
                    Text("Оформление", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = darkMode == null, onClick = { darkMode = null }, label = { Text("Система") })
                        FilterChip(selected = darkMode == false, onClick = { darkMode = false }, label = { Text("Светлая") })
                        FilterChip(selected = darkMode == true, onClick = { darkMode = true }, label = { Text("Тёмная") })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, bio, currency, darkMode, emergency, medical) },
                enabled = name.isNotBlank(),
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

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = PinkPrimary.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

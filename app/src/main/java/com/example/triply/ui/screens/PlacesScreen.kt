package com.example.triply.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.data.local.Place
import com.example.triply.ui.components.FeminineTextField
import com.example.triply.ui.theme.*
import com.example.triply.ui.viewmodels.PlacesViewModel
import com.example.triply.ui.viewmodels.PlacesViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    onViewMapClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: PlacesViewModel = viewModel(factory = PlacesViewModelFactory(repository, tripId))
    val places by viewModel.placesState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.favorite_places), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = PinkPrimary)
                    }
                },
                actions = {
                    // Кнопка перехода на общую карту всех мест
                    if (places.any { it.latitude != null }) {
                        IconButton(onClick = { onViewMapClick(tripId) }) {
                            Icon(Icons.Default.Map, contentDescription = "View on Map", tint = PinkPrimary)
                        }
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_place))
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (places.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_places), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(places, key = { it.id }) { place ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            PlaceItem(
                                place = place, 
                                onDelete = { viewModel.deletePlace(place) },
                                onOpenMaps = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddPlaceDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, address, desc ->
                    // Передаем context для геокодирования (поиска координат по адресу)
                    viewModel.addPlace(context, name, address, desc)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PlaceItem(place: Place, onDelete: () -> Unit, onOpenMaps: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkTertiary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = PinkPrimary)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(text = place.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                if (place.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = place.description, style = MaterialTheme.typography.bodySmall, color = PinkPrimary)
                }
            }

            IconButton(onClick = onOpenMaps) {
                Icon(Icons.Default.Map, contentDescription = stringResource(R.string.open_in_maps), tint = PinkPrimary.copy(alpha = 0.7f))
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = PinkSecondary)
            }
        }
    }
}

@Composable
fun AddPlaceDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = { Text(stringResource(R.string.save_place), fontWeight = FontWeight.Bold, color = PinkPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeminineTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.location), placeholder = stringResource(R.string.place_name_placeholder))
                FeminineTextField(value = address, onValueChange = { address = it }, label = stringResource(R.string.location), placeholder = stringResource(R.string.place_address_placeholder))
                FeminineTextField(value = desc, onValueChange = { desc = it }, label = stringResource(R.string.note), placeholder = stringResource(R.string.place_note_placeholder), singleLine = false)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, address, desc) },
                enabled = name.isNotBlank() && address.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = Color.Gray) }
        }
    )
}

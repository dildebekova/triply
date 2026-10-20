package com.example.triply.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.viewmodels.PlacesViewModel
import com.example.triply.ui.viewmodels.PlacesViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripMapScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: PlacesViewModel = viewModel(factory = PlacesViewModelFactory(repository, tripId))
    val places by viewModel.placesState.collectAsState()

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = true)
    }
    
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    // Default position (Paris) if no places exist
    val defaultPos = LatLng(48.8566, 2.3522)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 10f)
    }

    // Center camera on the first place with coordinates
    LaunchedEffect(places) {
        val firstPlaceWithCoords = places.firstOrNull { it.latitude != null && it.longitude != null }
        if (firstPlaceWithCoords != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstPlaceWithCoords.latitude!!, firstPlaceWithCoords.longitude!!),
                12f
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.places), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = PinkPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            ) {
                places.forEach { place ->
                    if (place.latitude != null && place.longitude != null) {
                        Marker(
                            state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                            title = place.name,
                            snippet = place.address
                        )
                    }
                }
            }
        }
    }
}

package com.example.triply.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.triply.R
import com.example.triply.TriplyApplication
import com.example.triply.data.local.Photo
import com.example.triply.ui.theme.PinkPrimary
import com.example.triply.ui.theme.PinkTertiary
import com.example.triply.ui.viewmodels.PhotosViewModel
import com.example.triply.ui.viewmodels.PhotosViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    tripId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as TriplyApplication).repository
    val viewModel: PhotosViewModel = viewModel(factory = PhotosViewModelFactory(repository, tripId))
    val photos by viewModel.photosState.collectAsState()

    var selectedPhotoForView by remember { mutableStateOf<Photo?>(null) }
    var photoToDelete by remember { mutableStateOf<Photo?>(null) }

    // Используем GetMultipleContents — самый надежный способ выбора файлов для эмуляторов
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                viewModel.addPhoto(uri.toString())
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.trip_gallery), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = PinkPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    launcher.launch("image/*") 
                },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_photo))
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (photos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.capture_moments), color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(photos, key = { it.id }) { photo ->
                        PhotoItem(
                            photo = photo, 
                            onDelete = { photoToDelete = photo },
                            onClick = { selectedPhotoForView = photo }
                        )
                    }
                }
            }
        }

        // Полноэкранный просмотр
        selectedPhotoForView?.let { photo ->
            FullScreenPhotoDialog(
                photo = photo,
                onDismiss = { selectedPhotoForView = null }
            )
        }

        // Подтверждение удаления
        photoToDelete?.let { photo ->
            AlertDialog(
                onDismissRequest = { photoToDelete = null },
                title = { Text("Удалить фото?") },
                text = { Text("Это действие нельзя будет отменить.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePhoto(photo)
                        photoToDelete = null
                    }) {
                        Text("Удалить", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { photoToDelete = null }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, onDelete: () -> Unit, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(PinkTertiary)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(28.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f),
            onClick = onDelete
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = Color.White,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}

@Composable
fun FullScreenPhotoDialog(photo: Photo, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = photo.uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

package dev.jpires.carview.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.viewmodel.ViewModel

@Composable
fun CarScreen(viewModel: ViewModel, navController: NavController) {
    CarViewForSpotifyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CarViewStructure(viewModel)
        }
    }
}

@Composable
fun CarViewStructure(viewModel: ViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Settings Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            CarButton(
                icon = Icons.Rounded.Settings,
                modifier = Modifier.size(32.dp)
            ) {
                // viewModel.navigateToSettings()
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrentlyPlaying(Modifier.weight(1f), viewModel)
            CarScreenMiddle(Modifier.weight(1f), viewModel)
            SongExtras(Modifier.weight(1f), viewModel)
        }
    }
}

@Composable
fun CurrentlyPlaying(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val track by viewModel.track.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = track?.artist?.name ?: "No Artist",
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = track?.name ?: "No Song",
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CarScreenMiddle(modifier: Modifier = Modifier, viewModel: ViewModel) {
    Box(modifier = modifier) {
        Column {
            SongProgress(viewModel = viewModel)
            SongControls(viewModel = viewModel)
        }
    }
}

@Composable
fun SongProgress(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val track by viewModel.track.collectAsState()
    val playbackPosition = viewModel.playbackPosition.collectAsState()

    val sliderPosition = if (track != null) {
        playbackPosition.value.toFloat() / track!!.duration.toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column {
            Slider(
                value = sliderPosition,
                valueRange = 0f..1f,
                onValueChange = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    text = viewModel.formatDuration(playbackPosition.value),
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = viewModel.formatDuration(track?.duration ?: 0),
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun SongControls(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val isPaused = viewModel.isPaused.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarButton(icon = Icons.Rounded.SkipPrevious, modifier = Modifier.size(120.dp)) {
                viewModel.skipPrevious()
            }
            CarButton(
                icon = if (isPaused.value) Icons.Rounded.PlayCircle else Icons.Rounded.PauseCircle,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(120.dp)
            ) {
                  viewModel.togglePlayPause()
            }
            CarButton(icon = Icons.Rounded.SkipNext, modifier = Modifier.size(120.dp)) {
                viewModel.skipNext()
            }
        }
    }
}

@Composable
fun SongExtras(modifier: Modifier = Modifier, viewModel: ViewModel) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarButton(icon = Icons.Rounded.Favorite, modifier = Modifier.size(64.dp)) {
//                viewModel.toggleFavorite()
            }
            Spacer(modifier = Modifier.width(64.dp))
            CarButton(icon = Icons.Rounded.Shuffle, modifier = Modifier.size(64.dp)) {
//                  viewModel.toggleShuffle()
            }
        }
    }
}

@Composable
fun CarButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            modifier = modifier,
            tint = tint
        )
    }
}
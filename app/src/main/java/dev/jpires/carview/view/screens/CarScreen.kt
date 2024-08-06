package dev.jpires.carview.view.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import dev.jpires.carview.view.navigation.Screen
import dev.jpires.carview.viewmodel.ViewModel
import java.util.logging.Logger

@Composable
fun CarScreen(viewModel: ViewModel, navController: NavController) {
    val isConnected by viewModel.isConnected.collectAsState()
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (!isConnected)
        navController.navigate(Screen.LoginScreen.route)

    Surface(modifier = Modifier.fillMaxSize()) {
        if (windowSize != WindowWidthSizeClass.EXPANDED)
            CarViewPortraitStructure(viewModel)
        else
            CarViewLandscapeStructure(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarViewLandscapeStructure(viewModel: ViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

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
                .navigationBarsPadding()
                .padding(8.dp)
        ) {
            CarButton(
                icon = Icons.Rounded.Settings,
                modifier = Modifier.size(32.dp)
            ) {
                showBottomSheet = true
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Ensure space between items
        ) {
            CurrentlyPlaying(Modifier.weight(0.2f), viewModel)
            CarScreenMiddle(Modifier.weight(0.5f), viewModel)
        }
    }

    if (showBottomSheet)
        BottomSheet(
            onDismiss = { showBottomSheet = false },
            viewModel = viewModel,
            sheetState = sheetState
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarViewPortraitStructure(viewModel: ViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

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
                showBottomSheet = true
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

    if (showBottomSheet)
        BottomSheet(
            onDismiss = { showBottomSheet = false },
            viewModel = viewModel,
            sheetState = sheetState
        )
}

@OptIn(ExperimentalFoundationApi::class)
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
                textAlign = TextAlign.Center,
                modifier = Modifier.basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    delayMillis = 0,
                    spacing = MarqueeSpacing(32.dp),
                    iterations = Int.MAX_VALUE
                )
            )
            Text(
                text = track?.name ?: "No Song",
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    delayMillis = 0,
                    spacing = MarqueeSpacing(32.dp),
                    iterations = Int.MAX_VALUE
                )
            )
        }
    }
}

@Composable
fun CarScreenMiddle(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    Box(modifier = modifier) {
        Column {
            SongProgress(viewModel = viewModel)
            if (windowSize != WindowWidthSizeClass.EXPANDED) SongControlsPortrait(viewModel = viewModel)
            else SongControlsLandscape(viewModel = viewModel)
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

    val movingSlider = rememberSaveable { mutableStateOf(false) }
    val sliderPos = rememberSaveable { mutableFloatStateOf(sliderPosition) }

    val pos by animateFloatAsState(
        targetValue = sliderPosition,
        label = "slider",
        animationSpec = tween(500)
    )

    val leftText = rememberSaveable { mutableStateOf(viewModel.formatDuration(playbackPosition.value)) }

    Box(
        modifier = modifier
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Slider(
                value = if (movingSlider.value) sliderPos.floatValue else pos,
                valueRange = 0f..1f,
                onValueChange = {
                    movingSlider.value = true
                    sliderPos.floatValue = it
                    leftText.value = viewModel.formatDuration((it * track!!.duration).toLong())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground
                ),
                onValueChangeFinished = {
                    movingSlider.value = false
                    if (viewModel.canSeek() && track != null)
                        viewModel.seekTo((sliderPos.floatValue * track!!.duration).toLong())
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (movingSlider.value) leftText.value else viewModel.formatDuration(playbackPosition.value),
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
fun SongControlsLandscape(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val isPaused = viewModel.isPaused.collectAsState()

    val isShuffled by viewModel.isShuffled.collectAsState()
    val isFavourite by viewModel.isFavourite.collectAsState()

    val animatedFavourite by animateColorAsState(
        targetValue = if (isFavourite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
        label = "tint"
    )

    val animatedShuffle by animateColorAsState(
        targetValue = if (isShuffled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
        label = "tint"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarButton(
                icon = Icons.Rounded.Favorite,
                modifier = Modifier.size(64.dp),
                tint = animatedFavourite
            ) {
                viewModel.toggleFavourite()
            }
            Spacer(modifier = Modifier.width(64.dp))
            CarButton(
                icon = Icons.Rounded.SkipPrevious,
                modifier = Modifier.size(120.dp),
                enabled = viewModel.canSkipPrevious()
            ) {
                viewModel.skipPrevious()
            }
            Spacer(modifier = Modifier.width(32.dp))
            CarButton(
                icon = if (isPaused.value) Icons.Rounded.PlayCircle else Icons.Rounded.PauseCircle,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(120.dp)
            ) {
                viewModel.togglePlayPause()
            }
            Spacer(modifier = Modifier.width(32.dp))
            CarButton(
                icon = Icons.Rounded.SkipNext,
                modifier = Modifier.size(120.dp),
                enabled = viewModel.canSkipNext()
            ) {
                viewModel.skipNext()
            }
            Spacer(modifier = Modifier.width(64.dp))
            CarButton(
                icon = Icons.Rounded.Shuffle,
                modifier = Modifier.size(64.dp),
                enabled = viewModel.canToggleShuffle(),
                tint = animatedShuffle
            ) {
                viewModel.toggleShuffle()
            }
        }
    }
}

@Composable
fun SongControlsPortrait(modifier: Modifier = Modifier, viewModel: ViewModel) {
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
            CarButton(
                icon = Icons.Rounded.SkipPrevious,
                modifier = Modifier.size(120.dp),
                enabled = viewModel.canSkipPrevious()
            ) {
                viewModel.skipPrevious()
            }
            CarButton(
                icon = if (isPaused.value) Icons.Rounded.PlayCircle else Icons.Rounded.PauseCircle,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(120.dp)
            ) {
                viewModel.togglePlayPause()
            }
            CarButton(
                icon = Icons.Rounded.SkipNext,
                modifier = Modifier.size(120.dp),
                enabled = viewModel.canSkipNext()
            ) {
                viewModel.skipNext()
            }
        }
    }
}

@Composable
fun SongExtras(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val isShuffled by viewModel.isShuffled.collectAsState()
    val isFavourite by viewModel.isFavourite.collectAsState()

    val animatedFavourite by animateColorAsState(
        targetValue = if (isFavourite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
        label = "tint"
    )

    val animatedShuffle by animateColorAsState(
        targetValue = if (isShuffled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
        label = "tint"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarButton(
                icon = Icons.Rounded.Favorite,
                modifier = Modifier.size(64.dp),
                tint = animatedFavourite
            ) {
                viewModel.toggleFavourite()
            }
            Spacer(modifier = Modifier.width(64.dp))
            CarButton(
                icon = Icons.Rounded.Shuffle,
                modifier = Modifier.size(64.dp),
                enabled = viewModel.canToggleShuffle(),
                tint = animatedShuffle
            ) {
                viewModel.toggleShuffle()
            }
        }
    }
}

@Composable
fun CarButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit, viewModel: ViewModel, sheetState: SheetState) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        tonalElevation = 16.dp
    ) {
        Column(
        ) {
            SettingsSheet(viewModel = viewModel)
        }
    }
}
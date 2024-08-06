package dev.jpires.carview.view.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import dev.jpires.carview.R
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.view.navigation.Screen
import dev.jpires.carview.viewmodel.ViewModel

@Composable
fun LoginScreen(viewModel: ViewModel, navController: NavController) {
    val isConnected by viewModel.isConnected.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()

    if (isConnected) {
        navController.navigate(Screen.CarScreen.route)
    }

    if (isConnecting)
        LoadingDialog()

    Surface(modifier = Modifier.fillMaxSize()) {
        LoginScreenStructure(viewModel, navController)
    }
}

@Composable
fun LoginScreenStructure(viewModel: ViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppLogo(Modifier.weight(1f))
        LoginText(Modifier.weight(1f))
        ConnectSpotifyButton(Modifier.weight(1f), viewModel)
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
fun LoginText(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Hello,",
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Connect to Spotify to continue",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ConnectSpotifyButton(modifier: Modifier = Modifier, viewModel: ViewModel) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                disabledContainerColor = Color.Gray
            ),
            enabled = true,
            onClick = {
                if (!viewModel.isSpotifyInstalled())
                    showDialog = true
                else
                    viewModel.connectToRemote()
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.spotify_primary_logo_rgb),
                    contentDescription = "Spotify Logo",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.width(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Connect to Spotify",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

    if (showDialog) {
        Alert(
            text = "Spotify is not installed. Do you want to install it from the Play Store?",
            dismissButtonText = "No",
            confirmButtonText = "Yes",
            icon = Icons.Rounded.Storefront,
            onDismiss = { showDialog = false }) {
            showDialog = false
            viewModel.connectToRemote()
        }
    }

}

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = { /* do nothing */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 5.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Alert(text: String, dismissButtonText: String, confirmButtonText: String, icon: ImageVector, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                )
            ) {
                Text(dismissButtonText, color = MaterialTheme.colorScheme.onBackground)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(confirmButtonText, color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}
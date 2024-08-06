package dev.jpires.carview.view.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.jpires.carview.R
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.view.navigation.Screen
import dev.jpires.carview.viewmodel.ViewModel
import java.util.logging.Logger

@Composable
fun LoginScreen(viewModel: ViewModel, navController: NavController) {
    val isConnected = viewModel.isConnected.collectAsState()

    if (isConnected.value) {
        navController.navigate(Screen.CarScreen.route)
    }

    CarViewForSpotifyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginScreenStructure(viewModel, navController)
        }
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
        ConnectSpotifyButton(Modifier.weight(1f), viewModel, navController)
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
fun ConnectSpotifyButton(modifier: Modifier = Modifier, viewModel: ViewModel, navController: NavController) {
//    val authToken by viewModel.authToken.collectAsState()

//    if (authToken != null) {
//        navController.navigate(Screen.CarScreen.route)
//    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = {
//                viewModel.initiateAuthFlow()
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
}
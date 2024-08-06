package dev.jpires.carview.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jpires.carview.model.data.ThemeMode
import dev.jpires.carview.viewmodel.ViewModel

@Composable
fun SettingsSheet(viewModel: ViewModel) {
    SettingsSheetStructure(viewModel)
}

@Composable
fun SettingsSheetStructure(viewModel: ViewModel) {
    val screenAlwaysOn by viewModel.screenAlwaysOn.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            DisconnectButton(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            SwitchSetting(text = "Keep Screen On", screenAlwaysOn) {
                viewModel.setScreenAlwaysOn(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            ButtonSetting(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DisconnectButton(viewModel: ViewModel) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showLoadingDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = Modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = Color.Gray
        ),
        onClick = {
            showDialog = true
        }
    ) {
        Text(
            text = "Disconnect",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    if (showDialog)
        Alert(
            text = "Are you sure you want to disconnect from Spotify?",
            dismissButtonText = "No",
            confirmButtonText = "Yes",
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.disconnectFromRemote()
                showDialog = false
                showLoadingDialog = true
            },
            icon = Icons.AutoMirrored.Rounded.ExitToApp
        )

    if (showLoadingDialog)
        LoadingDialog()
}

@Composable
fun SwitchSetting(text: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheck,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                uncheckedTrackColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
            )

        )
    }
}

@Composable
fun ButtonSetting(viewModel: ViewModel) {
    val theme by viewModel.themeMode.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Change App Theme",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                viewModel.toggleThemeMode()
            }
        ) {
            Icon(
                imageVector = when (theme) {
                    ThemeMode.DARK -> Icons.Rounded.DarkMode
                    ThemeMode.LIGHT -> Icons.Rounded.LightMode
                    ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
                },
                contentDescription = when (theme) {
                    ThemeMode.DARK -> "Dark Mode"
                    ThemeMode.LIGHT -> "Light Mode"
                    ThemeMode.SYSTEM -> "System Mode"
                },
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
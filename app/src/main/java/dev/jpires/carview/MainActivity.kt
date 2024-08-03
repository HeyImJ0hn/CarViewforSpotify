package dev.jpires.carview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import dev.jpires.carview.model.Repository
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.viewmodel.ViewModel
import dev.jpires.carview.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ViewModel
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = Repository(applicationContext)
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[ViewModel::class.java]

        setContent {
            CarViewForSpotifyTheme {
                Surface() {

                }
            }
        }
    }
}
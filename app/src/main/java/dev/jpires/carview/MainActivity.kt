package dev.jpires.carview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import dev.jpires.carview.model.Repository
import dev.jpires.carview.view.navigation.NavigationHost
import dev.jpires.carview.view.screens.LoginScreen
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
            NavigationHost(viewModel)
        }
    }
}
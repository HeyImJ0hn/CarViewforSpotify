package dev.jpires.carview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.jpires.carview.model.repository.DataStoreObject
import dev.jpires.carview.model.repository.Repository
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.view.navigation.NavigationHost
import dev.jpires.carview.view.navigation.Screen
import dev.jpires.carview.viewmodel.ViewModel
import dev.jpires.carview.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ViewModel
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dataStore = DataStoreObject.dataStore(applicationContext)

        repository = Repository(this, dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[ViewModel::class.java]

//        val splashScreen = installSplashScreen()

        lifecycleScope.launch {
            if (viewModel.isUserAuthenticated())
                viewModel.connectToRemote()

            setContent {
                NavigationHost(viewModel, Screen.LoginScreen.route)
            }
        }

//        splashScreen.apply {
//            setKeepOnScreenCondition {
//                if (isAuthenticated)
//                    !viewModel.isReady.value
//                else
//                    false
//            }
//        }

    }
}
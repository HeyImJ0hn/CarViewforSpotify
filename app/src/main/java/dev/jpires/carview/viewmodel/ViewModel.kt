package dev.jpires.carview.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.protocol.types.PlayerRestrictions
import com.spotify.protocol.types.Track
import dev.jpires.carview.model.data.ThemeMode
import dev.jpires.carview.model.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ViewModel(private val repository: Repository) : ViewModel() {
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: MutableStateFlow<Boolean> = _isConnecting

    private val _isConnected = MutableStateFlow(false)
    val isConnected: MutableStateFlow<Boolean> get() = _isConnected

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: MutableStateFlow<Boolean> get() = _isAuthenticated

    private val _isReady = MutableStateFlow(false)
    val isReady: MutableStateFlow<Boolean> get() = _isReady

    private val _track = MutableStateFlow<Track?>(null)
    val track: MutableStateFlow<Track?> get() = _track

    private val _isPaused = MutableStateFlow(true)
    val isPaused: MutableStateFlow<Boolean> get() = _isPaused

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: MutableStateFlow<Long> get() = _playbackPosition

    private val _isFavourite = MutableStateFlow(false)
    val isFavourite: MutableStateFlow<Boolean> get() = _isFavourite

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: MutableStateFlow<Boolean> get() = _isShuffled

    private val _playerRestrictions = MutableStateFlow<PlayerRestrictions?>(null)

    private val _screenAlwaysOn = MutableStateFlow(false)
    val screenAlwaysOn: MutableStateFlow<Boolean> get() = _screenAlwaysOn

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: MutableStateFlow<ThemeMode> get() = _themeMode

    private var updateJob: Job? = null

    init {
        checkAuthentication()

        loadTheme()

        observeTrack()
        observeIsPaused()
        observeFavourite()
        observeShuffled()

        observeScreenAlwaysOn()
    }

    private fun observeScreenAlwaysOn() {
        viewModelScope.launch {
            repository.screenAlwaysOn.collect {
                _screenAlwaysOn.value = it
            }
        }
    }

    private fun loadTheme() {
        viewModelScope.launch {
            repository.themeMode.collect { id ->
                _themeMode.value = ThemeMode.fromInt(id)
                cancel() // Cancel the scope when the theme is loaded, otherwise it will keep listening
            }
        }
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }

        viewModelScope.launch {
            repository.updateThemeMode(_themeMode.value.ordinal)
        }
    }

    fun connectToRemote() {
        _isConnecting.value = false

        if (!isSpotifyInstalled()) {
            repository.openSpotifyMarket()
            return
        }

        viewModelScope.launch {
            _isConnecting.value = true

            repository.connectToRemote()
            repository.isConnected.collect {
                _isConnected.value = it

                if (it) {
                    saveAuthKey("0")
                    startUpdatingPlaybackPosition()
                    delay(1000L)
                    _isReady.value = true
                    _isConnecting.value = false
                } else
                    stopUpdatingPlaybackPosition()
            }
        }
        updatePlayerRestrictions()
    }

    fun disconnectFromRemote() {
        viewModelScope.launch {
            repository.disconnectFromRemote()
            _isConnected.value = false
        }
    }

    private fun updatePlayerRestrictions() {
        viewModelScope.launch {
            repository.playerRestrictions.collect {
                _playerRestrictions.value = it
            }
        }
    }

    private fun observeTrack() {
        viewModelScope.launch {
            repository.track.collect { track ->
                _track.value = track
            }
        }
    }

    private fun observeIsPaused() {
        viewModelScope.launch {
            repository.isPaused.collect { isPaused ->
                _isPaused.value = isPaused
            }
        }
    }

    private fun observeFavourite() {
        viewModelScope.launch {
            repository.isFavourite.collect { favourite ->
                _isFavourite.value = favourite
            }
        }
    }

    private fun observeShuffled() {
        viewModelScope.launch {
            repository.isShuffled.collect { shuffled ->
                _isShuffled.value = shuffled
            }
        }
    }

    private fun startUpdatingPlaybackPosition() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(800)
                val position = repository.getCurrentPlaybackPosition()
                _playbackPosition.value = position
            }
        }
    }

    private fun stopUpdatingPlaybackPosition() {
        updateJob?.cancel()
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            repository.togglePlay()
        }
    }

    fun skipNext() {
        viewModelScope.launch {
            repository.skipNext()
        }
    }

    fun skipPrevious() {
        viewModelScope.launch {
            repository.skipPrevious()
        }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            repository.toggleShuffle()
        }
    }

    fun toggleFavourite() {
        viewModelScope.launch {
            repository.toggleFavourite()
        }
    }

    fun canSkipNext(): Boolean {
        return _playerRestrictions.value?.canSkipNext ?: false
    }

    fun canSkipPrevious(): Boolean {
        return _playerRestrictions.value?.canSkipPrev ?: false
    }

    fun canToggleShuffle(): Boolean {
        return _playerRestrictions.value?.canToggleShuffle ?: false
    }

    fun canSeek(): Boolean {
        return _playerRestrictions.value?.canSeek ?: false
    }

    fun seekTo(position: Long) {
        viewModelScope.launch {
            repository.seekTo(position)
        }
    }

    fun formatDuration(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.UK, "%02d:%02d", minutes, seconds)
    }

    private fun saveAuthKey(authKey: String) {
        viewModelScope.launch {
            repository.saveAuthKey(authKey)
        }
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = repository.isUserAuthenticated()
        }
    }

    suspend fun isUserAuthenticated(): Boolean {
        return repository.isUserAuthenticated()
    }

    fun isSpotifyInstalled(): Boolean {
        return repository.isSpotifyInstalled()
    }

    fun setScreenAlwaysOn(bool: Boolean) {
        viewModelScope.launch {
            repository.setScreenAlwaysOn(bool)
        }
    }

}
package dev.jpires.carview.viewmodel

import android.content.Intent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.protocol.types.Track
import dev.jpires.carview.model.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.logging.Logger

class ViewModel(private val repository: Repository) : ViewModel() {
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: MutableStateFlow<String?> = _authToken

    private val _isConnected = MutableStateFlow(false)
    val isConnected: MutableStateFlow<Boolean> get() = _isConnected

    private val _track = MutableStateFlow<Track?>(null)
    val track: MutableStateFlow<Track?> get() = _track

    private val _isPaused = MutableStateFlow(true)
    val isPaused: MutableStateFlow<Boolean> get() = _isPaused

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: MutableStateFlow<Long> get() = _playbackPosition

    private var updateJob: Job? = null

    init {
        observeTrack()
        observeIsPaused()
    }

    fun initiateAuthFlow() {
        viewModelScope.launch {
            repository.initiateAuthFlow { token ->
                _authToken.value = token
            }
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        repository.handleAuthResponse(requestCode, resultCode, intent)
    }

    fun connectToRemote() {
        viewModelScope.launch {
            repository.connectToRemote()
            repository.isConnected.collect {
                _isConnected.value = it

                if (it)
                    startUpdatingPlaybackPosition()
                else
                    stopUpdatingPlaybackPosition()
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

    private fun observePlaybackPosition() {
        viewModelScope.launch {
            repository.playbackPosition.collect { position ->
                _playbackPosition.value = position
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

    fun formatDuration(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.UK, "%02d:%02d", minutes, seconds)
    }


}
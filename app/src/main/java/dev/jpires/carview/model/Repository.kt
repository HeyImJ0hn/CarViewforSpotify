package dev.jpires.carview.model

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dev.jpires.carview.BuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class Repository(private val activity: Activity) {

    private val redirectUri = "https://carview.jpires.dev/callback"
    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID

    private var spotifyAppRemote: SpotifyAppRemote? = null

    private var onAuthResult: (String?) -> Unit = {}

    private val _isConnected = MutableStateFlow(false)
    val isConnected: MutableStateFlow<Boolean> get() = _isConnected

    private val _track = MutableStateFlow<Track?>(null)
    val track: MutableStateFlow<Track?> get() = _track

    private val _isPaused = MutableStateFlow(true)
    val isPaused: MutableStateFlow<Boolean> get() = _isPaused

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: MutableStateFlow<Long> get() = _playbackPosition

    fun initiateAuthFlow(onResult: (String?) -> Unit) {
        onAuthResult = onResult
        val builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)
        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
        activity.startActivityForResult(intent, REQUEST_CODE)
    }

    fun handleAuthResponse(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> onAuthResult(response.accessToken)
                AuthorizationResponse.Type.ERROR -> onAuthResult(null)
                else -> onAuthResult(null)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1337
    }

    fun connectToRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(activity.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                _isConnected.value = true
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                _isConnected.value = false
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    private fun connected() {
        spotifyAppRemote?.let { app ->
            // Subscribe to PlayerState
            app.playerApi.subscribeToPlayerState().setEventCallback {
                it.isPaused.let { isPaused ->
                    _isPaused.value = isPaused
                }
                it.playbackPosition.let { position ->
                    _playbackPosition.value = position
                }
                val track: Track = it.track
                _track.value = track
            }
        }
    }

    suspend fun getCurrentPlaybackPosition(): Long {
        return spotifyAppRemote?.playerApi?.playerState?.awaitCoroutine()?.playbackPosition ?: 0L
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun <T> CallResult<T>.awaitCoroutine(): T {
        return suspendCancellableCoroutine { continuation ->
            setResultCallback { result ->
                continuation.resume(result, null)
            }
            setErrorCallback { error ->
                continuation.resumeWithException(error)
            }
        }
    }

    fun togglePlay() {
        spotifyAppRemote?.let { app ->
            app.playerApi.playerState.setResultCallback {
                if (it.isPaused) {
                    app.playerApi.resume()
                } else {
                    app.playerApi.pause()
                }
            }
        }
    }

    fun skipNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun skipPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

}
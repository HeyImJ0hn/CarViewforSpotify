package dev.jpires.carview.model.repository

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.PlayerRestrictions
import com.spotify.protocol.types.Track
import dev.jpires.carview.BuildConfig
import dev.jpires.carview.model.data.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class Repository(private val activity: Activity, private val dataStore: DataStore<Preferences>) {

    private val redirectUri = "https://carview.jpires.dev/callback"
    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID

    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: MutableStateFlow<Boolean> get() = _isConnected

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
    val playerRestrictions: MutableStateFlow<PlayerRestrictions?> get() = _playerRestrictions

    private val authKeyKey = stringPreferencesKey("auth_key")
    private val screenAlwaysOnKey = booleanPreferencesKey("screen_always_on")
    private val themeModeKey = intPreferencesKey("theme_mode")

    val screenAlwaysOn = dataStore.data.map {
        if (it[screenAlwaysOnKey] != false)
            activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        it[screenAlwaysOnKey] ?: true
    }

    val themeMode = dataStore.data.map {
        it[themeModeKey] ?: 0
    }

    fun connectToRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(activity.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                _isConnected.value = false
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    suspend fun disconnectFromRemote() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            clearAuthKey()
        }
    }

    private fun connected() {
        spotifyAppRemote?.let { app ->
            app.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                _track.value = track

                it.isPaused.let { isPaused ->
                    _isPaused.value = isPaused
                }

                it.playbackPosition.let { position ->
                    _playbackPosition.value = position
                }

                it.playbackOptions.isShuffling.let { isShuffled ->
                    _isShuffled.value = isShuffled
                }

                spotifyAppRemote?.userApi?.getLibraryState(track.uri)?.setResultCallback { item ->
                    _isFavourite.value = item.isAdded
                }

                _playerRestrictions.value = it.playbackRestrictions
            }

            _isConnected.value = true
        }
    }

    suspend fun getCurrentPlaybackPosition(): Long {
        return if (spotifyAppRemote?.isConnected!!) spotifyAppRemote?.playerApi?.playerState?.awaitCoroutine()?.playbackPosition ?: 0L else 0L
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

    fun toggleShuffle() {
        spotifyAppRemote?.playerApi?.toggleShuffle()
    }

    fun toggleFavourite() {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback {
            val track = it.track
            spotifyAppRemote?.userApi?.getLibraryState(track.uri)?.setResultCallback { item ->
                _isFavourite.value = item.isAdded
                if (item.isAdded) {
                    spotifyAppRemote?.userApi?.removeFromLibrary(track.uri)
                } else {
                    spotifyAppRemote?.userApi?.addToLibrary(track.uri)
                }
            }
        }
    }

    fun seekTo(position: Long) {
        spotifyAppRemote?.playerApi?.seekTo(position)
    }

    suspend fun saveAuthKey(authKey: String) {
        dataStore.edit { preferences ->
            preferences[authKeyKey] = authKey
        }
    }

    private suspend fun clearAuthKey() {
        dataStore.edit { preferences ->
            preferences.remove(authKeyKey)
        }
    }

    suspend fun isUserAuthenticated(): Boolean {
        val currentAuthKey = dataStore.data.first()[authKeyKey] ?: ""
        return currentAuthKey.isNotEmpty()
    }

    fun isSpotifyInstalled(): Boolean {
        val pm = activity.packageManager
        return try {
            pm.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openSpotifyMarket() {
        try {
            startPlayStoreActivity("market://details")
        } catch (ignored: ActivityNotFoundException) {
            startPlayStoreActivity("https://play.google.com/store/apps/details")
        }
    }

    private fun startPlayStoreActivity(uriString : String) {
        val branchLink = Uri.encode("https://spotify.link/content_linking?~campaign=" + activity.applicationContext.packageName)
        val referrer = "_branch_link=$branchLink"
        val uri = Uri.parse(uriString)
            .buildUpon()
            .appendQueryParameter("id", "com.spotify.music")
            .appendQueryParameter("referrer", referrer)
            .build()
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    suspend fun setScreenAlwaysOn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[screenAlwaysOnKey] = value
        }

        if (value)
            activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    suspend fun updateThemeMode(value: Int) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = value
        }
    }

}
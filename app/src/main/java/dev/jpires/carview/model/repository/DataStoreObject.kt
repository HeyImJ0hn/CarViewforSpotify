package dev.jpires.carview.model.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object DataStoreObject {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "carview")

    fun dataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
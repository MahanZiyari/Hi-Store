package ir.mahan.histore.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mahan.histore.util.constants.SESSION_AUTH_DATA
import ir.mahan.histore.util.constants.USER_TOKEN_DATA
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    val appContext = context.applicationContext

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            SESSION_AUTH_DATA
        )
        private val userTokenKey = stringPreferencesKey(USER_TOKEN_DATA)
    }

    suspend fun saveToken(token: String) {
        appContext.dataStore.edit {
            it[userTokenKey] = token
        }
    }

    val authToken = appContext.dataStore.data.map {
        it[userTokenKey]
    }

    suspend fun clearToken() {
        appContext.dataStore.edit {
            it.clear()
        }
    }
}
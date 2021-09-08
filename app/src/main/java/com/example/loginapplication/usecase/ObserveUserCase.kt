package com.example.loginapplication.usecase

import com.example.loginapplication.model.domain.User
import com.example.loginapplication.util.DATASTORE_LOGGED_IN_EMAIL_KEY
import com.example.loginapplication.util.DatastoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {

    operator fun invoke() : Flow<User?> {
        return datastoreManager.observeKeyValue(DATASTORE_LOGGED_IN_EMAIL_KEY).map {
            if (it.isNullOrEmpty()) null else User(it)
        }

    }
}
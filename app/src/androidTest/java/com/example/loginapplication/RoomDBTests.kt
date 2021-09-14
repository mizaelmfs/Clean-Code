package com.example.loginapplication

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.example.loginapplication.model.db.ApplicationDatabase
import com.example.loginapplication.model.db.dao.UserDAO
import com.example.loginapplication.model.db.dto.UserDTO
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class RoomDBTests {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: ApplicationDatabase
    private lateinit var dao: UserDAO

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.userDAO()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun insetValidUser() = runBlocking {
        val email = "email@email.com"
        val password = "password"
        val user = UserDTO(
            email = email,
            password = password
        )
        dao.insert(user)
        val isUser = dao.isRowIsExist(email)
        assertThat(isUser, IsEqual.equalTo(true))
    }

}
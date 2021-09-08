package com.example.loginapplication.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.loginapplication.model.db.dto.UserDTO

@Dao
interface UserDAO {

    @Query("SELECT * FROM UserDTO WHERE email = :email")
    suspend fun findByEmail(email: String) : UserDTO

    @Query("SELECT EXISTS(SELECT * FROM UserDTO WHERE email = :email)")
    suspend fun isRowIsExist(email: String) : Boolean

    @Insert
    suspend fun insert(userDTO: UserDTO)
}
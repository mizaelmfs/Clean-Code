package com.example.loginapplication.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.loginapplication.model.db.dto.UserDTO
import com.example.loginapplication.model.db.dao.UserDAO

@Database(entities = [UserDTO::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun userDAO() : UserDAO
}
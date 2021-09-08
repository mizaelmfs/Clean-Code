package com.example.loginapplication.usecase

import com.example.loginapplication.model.db.dao.UserDAO
import com.example.loginapplication.model.db.dto.UserDTO
import timber.log.Timber
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val userDAO: UserDAO
) {
    suspend operator fun invoke(userDTO: UserDTO) {
        Timber.d("invoke!")
        userDAO.insert(userDTO = userDTO)
    }
}
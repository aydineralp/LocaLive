package com.veyselaydineralp.localive.model

data class RegisterRequest(
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userPassword: String,
    val userSurname: String,
    val userDateOfBirth: String,
    val preferences: String,
    val EventRange: String

)

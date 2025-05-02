package com.veyselaydineralp.localive.model

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val data: Data?
)

// Data model
data class Data(
    val userId: Int,
    val userName: String?
)



package com.commoncents.ui.auth.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("status")
    val status: String? = null
)


fun LoginResponse.nullCheck(): LoginResponse {
    return this.copy(
        code = code,
        status = status,
        data = data?.nullCheck()
    )
}


data class Data(

    @field:SerializedName("access_token")
    var accessToken: String? = null,

    @field:SerializedName("refresh_token")
    var refreshToken: String? = null,

    @field:SerializedName("user")
    var user: User? = null,

    )

fun Data.nullCheck(): Data {
    return this.copy(
        accessToken = accessToken ?: "",
        refreshToken = refreshToken ?: "",
        user = user?.nullCheck()
    )
}


data class User(
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("phone_number")
    var phoneNumber: String? = null,

    @field:SerializedName("image")
    var image: String? = null
)

fun User.nullCheck(): User {
    return this.copy(
        id = id ?: 0,
        email = email ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: "",
        image = image ?: ""
    )
}


package com.commoncents.data.repository

import com.commoncents.data.api.ApiHelper
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiHelper: ApiHelper
) {
    suspend fun login(
        userName: String,
        password: String
    ) = apiHelper.login(
        userName,
        password
    )


    suspend fun refreshToken(refreshToken: String) = apiHelper.refreshToken(refreshToken)


}

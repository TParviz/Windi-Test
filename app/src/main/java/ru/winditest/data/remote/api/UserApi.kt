package ru.winditest.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.winditest.data.remote.api.model.user.UserDto
import ru.winditest.data.remote.api.model.user.EditUserInfoRequest
import ru.winditest.data.remote.api.model.user.EditUserInfoResponse

interface UserApi {

    @GET("users/me/")
    suspend fun getCurrentUser(): UserDto

    @PUT("users/me/")
    suspend fun changeUserInfo(@Body userInfo: EditUserInfoRequest): EditUserInfoResponse
}
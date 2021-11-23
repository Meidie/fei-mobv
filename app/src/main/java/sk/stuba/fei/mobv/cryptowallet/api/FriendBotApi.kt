package sk.stuba.fei.mobv.cryptowallet.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FriendBotApi {

    @GET("/")
    suspend fun createAccount(@Query("addr") publicKey: String): Response<Void>
}
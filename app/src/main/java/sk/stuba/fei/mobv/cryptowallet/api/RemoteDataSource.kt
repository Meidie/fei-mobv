package sk.stuba.fei.mobv.cryptowallet.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteDataSource {

    private const val FRIEND_BOT_BASE_URL = "https://friendbot.stellar.org"
    const val TESTNET_STELLAR_BASE_URL = "https://horizon-testnet.stellar.org"

    @Volatile
    private var STELLAR_API_INSTANCE: StellarApi? = null

    fun getStellarApi(): StellarApi {

        return STELLAR_API_INSTANCE ?: synchronized(this) {
            val instance = StellarApi()
            STELLAR_API_INSTANCE = instance
            return instance
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(FRIEND_BOT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val friendBotApi: FriendBotApi by lazy {
        retrofit.create(FriendBotApi::class.java)
    }
}
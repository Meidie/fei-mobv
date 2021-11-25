package sk.stuba.fei.mobv.cryptowallet.api

import android.util.Log
import org.stellar.sdk.*
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse

class StellarApi {

    fun getAccount(accountId: String): AccountResponse? {

        return runCatching {
            val server = Server(RemoteDataSource.TESTNET_STELLAR_BASE_URL)
            server.accounts().account(accountId)
        }.onSuccess {
            Log.d("STELLAR API", "Getting account $accountId was successful")
        }.onFailure {
            Log.e(
                "STELLAR API",
                "Error trying to get account $accountId, error: ${it.localizedMessage}"
            )
        }.getOrNull()
    }

    fun getTransactions() {

    }

    fun getBalance() {

    }

    fun sendTransaction(transaction: Transaction): SubmitTransactionResponse? {

        return runCatching {
            val server = Server(RemoteDataSource.TESTNET_STELLAR_BASE_URL)
            server.submitTransaction(transaction)
        }.onSuccess {
            Log.d("STELLAR API", "Add transaction was successful")
        }.onFailure {
            Log.e(
                "STELLAR API",
                "Error trying to add transaction, error: ${it.localizedMessage}"
            )
        }.getOrNull()
    }
}
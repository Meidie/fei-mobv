package sk.stuba.fei.mobv.cryptowallet.api

import android.util.Log
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.responses.operations.OperationResponse
import java.util.ArrayList

class StellarApi {

    fun getAccount(accountId: String): AccountResponse? {

        return runCatching {
            val server = Server(RemoteDataSource.TESTNET_STELLAR_BASE_URL)
            server.accounts().account(accountId)
        }.onSuccess {
            Log.d("STELLAR API", "Getting account $accountId request was successful")
        }.onFailure {
            Log.e(
                "STELLAR API",
                "Error trying to get account $accountId, error: ${it.localizedMessage}"
            )
        }.getOrNull()
    }

    fun getTransactions(accountId: String) : ArrayList<OperationResponse>? {
        return runCatching {
            val server = Server(RemoteDataSource.TESTNET_STELLAR_BASE_URL)
            val paymentsRequest = server.payments().forAccount(accountId).limit(200)
            val data = paymentsRequest.execute()
           data.records
        }.onSuccess {
            Log.d("STELLAR API", "Getting transactions request was successful")
        }.onFailure {
            Log.e(
                "STELLAR API",
                "Error trying to get transactions, error: ${it.localizedMessage}"
            )
        }.getOrNull()
    }

    fun sendTransaction(transaction: Transaction): SubmitTransactionResponse? {

        return runCatching {
            val server = Server(RemoteDataSource.TESTNET_STELLAR_BASE_URL)
            server.submitTransaction(transaction)
        }.onSuccess {
            Log.d("STELLAR API", "Transaction sent request was successful")
        }.onFailure {
            Log.e(
                "STELLAR API",
                "Error trying to send transaction, error: ${it.localizedMessage}"
            )
        }.getOrNull()
    }
}
package sk.stuba.fei.mobv.cryptowallet.repository

import android.util.Log
import androidx.lifecycle.LiveData
import org.stellar.sdk.AssetTypeNative
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.dao.TransactionDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionAndContact
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionRepository(private val dao: TransactionDao, private val api: StellarApi) : IRepository<Transaction> {

    override suspend fun find(id: Long): Transaction? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Transaction): Long {
        return dao.insert(entity)
    }

    override suspend fun update(entity: Transaction) {
        dao.update(entity)
    }

    override suspend fun delete(entity: Transaction) {
        dao.delete(entity)
    }

    fun getAllTransactions(): LiveData<List<Transaction>> {
        return dao.getAllTransactionsActiveAccount()
    }

    fun getAllTransactionAndContact(): LiveData<List<TransactionAndContact>> {
        return dao.getAllTransactionsAndContactsActiveAccount()
    }

    fun getAllTransactionWithoutContact(): LiveData<List<Transaction>> {
        return dao.getAllTransactionsWithoutContactsActiveAccount()
    }

    suspend fun sendTransaction(sourceAccount: Account, stellarAccount: AccountResponse,
        transaction: Transaction, pin: String
    ): SubmitTransactionResponse? {

        transaction.accountOwnerId = sourceAccount.accountId

        val newTransaction: org.stellar.sdk.Transaction = org.stellar.sdk.Transaction.Builder(
            stellarAccount, Network.TESTNET)
            .addOperation(
                PaymentOperation.Builder(transaction.publicKey, AssetTypeNative(), transaction.amount)
                    .build()
            )
            .setTimeout(180)
            .setBaseFee(org.stellar.sdk.Transaction.MIN_BASE_FEE)
            .build()

        val sk = Crypto().decrypt(sourceAccount.privateKeyData!!, pin)
        Log.d("STELLAR API", sk)
        val source: KeyPair = KeyPair.fromSecretSeed(sk)
        newTransaction.sign(source)

        val response = api.sendTransaction(newTransaction)

        if (response != null) {
            transaction.dateTime =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
            dao.insert(transaction)
        }

        return response
    }
}
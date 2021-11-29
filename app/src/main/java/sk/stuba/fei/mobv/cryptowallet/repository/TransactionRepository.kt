package sk.stuba.fei.mobv.cryptowallet.repository

import android.util.Log
import androidx.lifecycle.LiveData
import org.stellar.sdk.AssetTypeNative
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.responses.operations.CreateAccountOperationResponse
import org.stellar.sdk.responses.operations.OperationResponse
import org.stellar.sdk.responses.operations.PaymentOperationResponse
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.dao.TransactionDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionAndContact
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList

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
        amount: String, publicKey: String, pin: String
    ): SubmitTransactionResponse? {

        val newStellarTransaction: org.stellar.sdk.Transaction = org.stellar.sdk.Transaction.Builder(
            stellarAccount, Network.TESTNET)
            .addOperation(
                PaymentOperation.Builder(publicKey, AssetTypeNative(), amount)
                    .build()
            )
            .setTimeout(180)
            .setBaseFee(org.stellar.sdk.Transaction.MIN_BASE_FEE)
            .build()

        val sk = Crypto().decrypt(sourceAccount.privateKeyData!!, pin)
        Log.d("STELLAR API", sk)
        val source: KeyPair = KeyPair.fromSecretSeed(sk)
        newStellarTransaction.sign(source)

        val response = api.sendTransaction(newStellarTransaction)

        if (response != null) {
            val newTransaction = Transaction(response.hash, sourceAccount.accountId, amount,
                TransactionType.DEBET, publicKey,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
            )
            dao.insert(newTransaction)
        }

        return response
    }

    suspend fun syncTransactions(account: Account): ArrayList<OperationResponse>? {

        val response = api.getTransactions(account.publicKey)
        response?.forEach {

            var ammount = ""
            if (it is CreateAccountOperationResponse) {
                ammount = it.startingBalance
            } else if (it is PaymentOperationResponse){
                ammount = it.amount
            }

            val transaction = Transaction(it.transactionHash, account.accountId, ammount,
                if (it.sourceAccount == account.publicKey) TransactionType.DEBET else TransactionType.CREDIT,
                account.publicKey, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")))
            dao.insert(transaction)
        }

        return response
    }
}
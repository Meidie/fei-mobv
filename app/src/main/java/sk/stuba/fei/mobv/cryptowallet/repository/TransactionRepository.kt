package sk.stuba.fei.mobv.cryptowallet.repository

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
import java.time.ZoneOffset
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
        val source: KeyPair = KeyPair.fromSecretSeed(sk)
        newStellarTransaction.sign(source)

        val response = api.sendTransaction(newStellarTransaction)

        if (response!= null && response.isSuccess) {
            val newTransaction = Transaction(response.hash, sourceAccount.accountId, amount,
                TransactionType.DEBET, publicKey,
                LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
            )
            dao.insert(newTransaction)
        }

        return response
    }

    private fun parseDate(createdAt: String): String {
        val dateTime: LocalDateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    }

    suspend fun syncTransactions(account: Account): ArrayList<OperationResponse>? {

        val response = api.getTransactions(account.publicKey)
        response?.forEach {

            val transaction = Transaction(
                it.transactionHash, account.accountId, "",
                TransactionType.DEBET, "", parseDate(it.createdAt)
            )

            if (it is CreateAccountOperationResponse) {
                transaction.amount = it.startingBalance
                transaction.publicKey = it.funder
                transaction.type = TransactionType.CREDIT
            } else if (it is PaymentOperationResponse) {
                transaction.amount = it.amount
                if (account.publicKey == it.from) {
                    transaction.type = TransactionType.DEBET
                    transaction.publicKey = it.to
                } else {
                    transaction.type = TransactionType.CREDIT
                    transaction.publicKey = it.from
                }
            }
            dao.insert(transaction)
        }

        return response
    }
}
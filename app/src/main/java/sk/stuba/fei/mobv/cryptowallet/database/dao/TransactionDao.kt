package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionAndContact

@Dao
interface TransactionDao : IDao<Transaction> {

    @Query("DELETE FROM transaction_table")
    suspend fun deleteAll()

    @Query("SELECT * from transaction_table WHERE transactionId = :id")
    suspend fun find(id: Long): Transaction?

    @Query("SELECT * FROM transaction_table ORDER BY transactionId DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE account_owner_id = ( select accountId from account_table where active_account)")
    fun getAllTransactionsActiveAccount(): LiveData<List<Transaction>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transaction_table t WHERE t.account_owner_id = ( select accountId from account_table where active_account) AND EXISTS (SELECT * FROM contact_table c where c.public_key = t.public_key)")
    fun getAllTransactionsAndContactsActiveAccount(): LiveData<List<TransactionAndContact>>

    @Query("SELECT * FROM transaction_table t WHERE t.account_owner_id = ( select accountId from account_table where active_account) AND NOT EXISTS (SELECT * FROM contact_table c where c.public_key = t.public_key)")
    fun getAllTransactionsWithoutContactsActiveAccount(): LiveData<List<Transaction>>
}
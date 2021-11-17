package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.StellarTransaction

@Dao
interface TransactionDao : IDao<StellarTransaction> {

    @Query("DELETE FROM transaction_table")
    suspend fun deleteAll()

    @Query("SELECT * from transaction_table WHERE transactionId = :id")
    suspend fun find(id: Long): StellarTransaction?

    @Query("SELECT * FROM transaction_table ORDER BY transactionId DESC")
    fun getAllTransactions(): LiveData<List<StellarTransaction>>
}
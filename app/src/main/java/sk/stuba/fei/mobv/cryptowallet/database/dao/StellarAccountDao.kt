package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithTransactions
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account

@Dao
interface StellarAccountDao : IDao<Account> {

    @Query("DELETE FROM stellar_account_table")
    suspend fun deleteAll()

    @Query("SELECT * from stellar_account_table WHERE accountId = :id")
    suspend fun find(id: Long): Account?

    @androidx.room.Transaction
    @Query("SELECT * FROM stellar_account_table WHERE accountId = :id")
    fun getAccountsWithTransactionsContacts(id: Long): LiveData<List<AccountWithTransactions>>
}
package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithTransactions
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithBalances
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithContacts

@Dao
interface AccountDao : IDao<Account> {

    @Query("DELETE FROM account_table")
    suspend fun deleteAll()

    @Query("SELECT * from account_table WHERE accountId = :id")
    suspend fun find(id: Long): Account?


    @Query("SELECT * from account_table WHERE public_key = :id")
    suspend fun findByPublicKey(id: String): Account?


    @Query("SELECT * from account_table WHERE active_account")
    suspend fun findActive(): Account

    @Query("SELECT EXISTS(SELECT 1 FROM account_table  WHERE  active_account);")
    suspend fun doesActiveAccountsExistAsync() : Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM account_table  WHERE  active_account);")
    fun doesActiveAccountsExist() : LiveData<Boolean>

    @androidx.room.Transaction
    @Query("SELECT * FROM account_table WHERE accountId = :id")
    fun getAccountsWithTransactionsContacts(id: Long): LiveData<List<AccountWithTransactions>>

    @androidx.room.Transaction
    @Query("SELECT * FROM account_table WHERE accountId = :id")
    fun getAccountsWithContactsContacts(id: Long): LiveData<List<AccountWithContacts>>

    @androidx.room.Transaction
    @Query("SELECT * FROM account_table WHERE accountId = :id")
    fun getAccountsWithBalancesContacts(id: Long): LiveData<List<AccountWithBalances>>
}
package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance

@Dao
interface BalanceDao : IDao<Balance> {

    @Query("DELETE FROM balance_table")
    suspend fun deleteAll()

    @Query("SELECT * from balance_table WHERE balanceId = :id")
    suspend fun find(id: Long): Balance?

    @Query("SELECT * FROM balance_table WHERE account_owner_id = (SELECT accountId  FROM account_table WHERE active_account)")
    fun getAllBalances(): LiveData<List<Balance>>

    @Query("UPDATE balance_table SET amount = :amount WHERE currency = :currency and account_owner_id = :accountId")
    fun updateAmount(amount: String, currency: String, accountId: Long)

    @Query("SELECT amount FROM balance_table WHERE account_owner_id = ( select accountId from account_table where active_account) AND currency = :currency")
    fun getAmount(currency: String): String
}
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

    @Query("SELECT * FROM balance_table")
    fun getAllBalances(): LiveData<List<Balance>>

    @Query("UPDATE balance_table SET amount=:amount WHERE currency = :currency")
    fun updateAmount(amount: String, currency: String)

    @Query("SELECT amount FROM balance_table WHERE account_owner_id = ( select accountId from account_table where active_account) AND currency = :currency")
    fun getAmount(currency: String): LiveData<String>
}
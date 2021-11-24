package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithContacts
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

@Dao
interface ContactDao : IDao<Contact> {

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()

    @Query("SELECT * from contact_table WHERE contactId = :id")
    suspend fun find(id: Long): Contact?

    @Query("SELECT * FROM contact_table ORDER BY contact_name DESC")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT  *  FROM contact_table WHERE account_owner_id = (SELECT  accountId  FROM account_table WHERE active_account)")
    fun getAllContactActiveAccount(): LiveData<List<Contact>>

}
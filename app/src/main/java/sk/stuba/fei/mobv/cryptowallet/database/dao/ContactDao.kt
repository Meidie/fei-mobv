package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

@Dao
interface ContactDao : IDao<Contact> {

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()

    @Query("SELECT * from contact_table WHERE contactId = :id")
    suspend fun find(id: Long): Contact?

    @Query("SELECT * FROM contact_table ORDER BY contact_first_name DESC")
    fun getAllContacts(): LiveData<List<Contact>>
}
package sk.stuba.fei.mobv.cryptowallet.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

@Dao
interface ContactDao {

    @Query("SELECT * from contact_table WHERE id = :id")
    suspend fun find(id: Long): Contact?

    @Query("SELECT * FROM contact_table ORDER BY contact_first_name DESC")
    fun getAllContacts(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(vararg contact: Contact)

    @Delete
    suspend fun delete(vararg contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()
}
package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.ContactDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

class ContactRepository(private val dao: ContactDao) : IRepository<Contact> {

    override suspend fun find(id: Long) : Contact? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Contact) {
        dao.insert(entity)
    }

    override suspend fun update(entity: Contact) {
        dao.update(entity)
    }

    override suspend fun delete(entity: Contact) {
        dao.delete(entity)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    fun getAllContacts() : LiveData<List<Contact>> {
        return dao.getAllContacts()
    }
}
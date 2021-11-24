package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.ContactDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

class ContactRepository(private val contactDao: ContactDao) : IRepository<Contact> {

    override suspend fun find(id: Long): Contact? {
        return contactDao.find(id)
    }

    override suspend fun insert(entity: Contact): Long {
        return contactDao.insert(entity)
    }

    override suspend fun update(entity: Contact) {
        contactDao.update(entity)
    }

    override suspend fun delete(entity: Contact) {
        contactDao.delete(entity)
    }

    suspend fun deleteAll() {
        contactDao.deleteAll()
    }

    fun getAllContacts(): LiveData<List<Contact>> {
        return contactDao.getAllContactActiveAccount()
    }
}
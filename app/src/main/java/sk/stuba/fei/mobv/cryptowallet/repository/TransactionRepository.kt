package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.TransactionDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction

class TransactionRepository(private val dao: TransactionDao) : IRepository<Transaction> {

    override suspend fun find(id: Long) : Transaction? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Transaction) {
        dao.insert(entity)
    }

    override suspend fun update(entity: Transaction) {
        dao.update(entity)
    }

    override suspend fun delete(entity: Transaction) {
        dao.delete(entity)
    }

    fun getAllTransactions() : LiveData<List<Transaction>> {
        return dao.getAllTransactions()
    }
}
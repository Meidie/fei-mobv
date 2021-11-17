package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.TransactionDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.StellarTransaction

class TransactionRepository(private val dao: TransactionDao) : IRepository<StellarTransaction> {

    override suspend fun find(id: Long) : StellarTransaction? {
        return dao.find(id)
    }

    override suspend fun insert(entity: StellarTransaction) {
        dao.insert(entity)
    }

    override suspend fun update(entity: StellarTransaction) {
        dao.update(entity)
    }

    override suspend fun delete(entity: StellarTransaction) {
        dao.delete(entity)
    }

    fun getAllTransactions() : LiveData<List<StellarTransaction>> {
        return dao.getAllTransactions()
    }
}
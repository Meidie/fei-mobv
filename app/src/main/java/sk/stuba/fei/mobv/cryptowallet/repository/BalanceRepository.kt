package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.BalanceDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance

class BalanceRepository(private val dao: BalanceDao) : IRepository<Balance> {

    override suspend fun find(id: Long): Balance? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Balance): Long {
        return dao.insert(entity)
    }

    override suspend fun update(entity: Balance) {
        dao.update(entity)
    }

    override suspend fun delete(entity: Balance) {
        dao.delete(entity)
    }

    fun getAllBalances(): LiveData<List<Balance>> {
        return dao.getAllBalances()
    }
}
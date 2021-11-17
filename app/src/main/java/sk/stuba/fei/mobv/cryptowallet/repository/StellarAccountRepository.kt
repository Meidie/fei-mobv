package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.StellarAccountDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithTransactions
import sk.stuba.fei.mobv.cryptowallet.database.entity.StellarAccount

class StellarAccountRepository(private val dao: StellarAccountDao) : IRepository<StellarAccount> {

    override suspend fun find(id: Long): StellarAccount? {
        return dao.find(id)
    }

    override suspend fun insert(entity: StellarAccount) {
        dao.insert(entity)
    }

    override suspend fun update(entity: StellarAccount) {
        dao.delete(entity)
    }

    override suspend fun delete(entity: StellarAccount) {
        dao.delete(entity)
    }

    fun getAccountsWithTransactionsContacts(id: Long): LiveData<List<AccountWithTransactions>> {
        return dao.getAccountsWithTransactionsContacts(id)
    }
}
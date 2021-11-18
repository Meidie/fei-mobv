package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import sk.stuba.fei.mobv.cryptowallet.database.dao.AccountDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithTransactions
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account

class AccountRepository(private val dao: AccountDao) : IRepository<Account> {

    override suspend fun find(id: Long): Account? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Account) {
        dao.insert(entity)
    }

    override suspend fun update(entity: Account) {
        dao.delete(entity)
    }

    override suspend fun delete(entity: Account) {
        dao.delete(entity)
    }

    fun getAccountsWithTransactionsContacts(id: Long): LiveData<List<AccountWithTransactions>> {
        return dao.getAccountsWithTransactionsContacts(id)
    }
}
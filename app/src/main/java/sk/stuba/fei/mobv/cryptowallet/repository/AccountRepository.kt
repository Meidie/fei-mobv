package sk.stuba.fei.mobv.cryptowallet.repository

import org.stellar.sdk.responses.AccountResponse
import retrofit2.Response
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.dao.AccountDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account

class AccountRepository(private val dao: AccountDao, private val api: StellarApi) :
    IRepository<Account> {

    override suspend fun find(id: Long): Account? {
        return dao.find(id)
    }

    override suspend fun insert(entity: Account): Long {
        return dao.insert(entity)
    }

    override suspend fun update(entity: Account) {
        dao.delete(entity)
    }

    override suspend fun delete(entity: Account) {
        dao.delete(entity)
    }

    suspend fun getActiveAccount(): Account {
        return dao.findActive()
    }

    fun getAccountInfo(accountId: String): AccountResponse? {
        return api.getAccount(accountId)
    }

    suspend fun createAccount(accountId: String): Response<Void> {
        return RemoteDataSource.friendBotApi.createAccount(accountId)
    }

    suspend fun signOut(){
        val activeAccount = dao.findActive()
        activeAccount.active = false
        dao.update(activeAccount)
    }
}
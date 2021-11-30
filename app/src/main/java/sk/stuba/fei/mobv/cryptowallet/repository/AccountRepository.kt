package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
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

    suspend fun findByPublicKey(id: String): Account? {
        return dao.findByPublicKey(id)
    }

    suspend fun doesActiveAccountsExistAsync(): Boolean {
        return dao.doesActiveAccountsExistAsync()
    }

    override suspend fun insert(entity: Account): Long {
        return dao.insert(entity)
    }

    override suspend fun update(entity: Account) {
        dao.update(entity)
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

    fun doesActiveAccountsExist(): LiveData<Boolean> {
        return dao.doesActiveAccountsExist()
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
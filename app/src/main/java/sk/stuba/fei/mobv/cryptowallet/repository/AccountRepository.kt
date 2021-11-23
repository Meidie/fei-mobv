package sk.stuba.fei.mobv.cryptowallet.repository

import androidx.lifecycle.LiveData
import org.stellar.sdk.KeyPair
import retrofit2.Response
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.dao.AccountDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.AccountWithTransactions
import sk.stuba.fei.mobv.cryptowallet.security.Crypto

class AccountRepository(private val dao: AccountDao, private val api: StellarApi) : IRepository<Account> {

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

    suspend fun createAccount(pin: String) : Response<Void> {

        // TODO pridat progress bar v UI lebo to trva nejaku dobu
        val pair: KeyPair = KeyPair.random()
        val response = RemoteDataSource.friendBotApi.createAccount(pair.accountId)

        // TODO ulozit do DB -> potrebne na desifrovanie
        val cipherData = Crypto().encrypt(String(pair.secretSeed), pin)

        if(response.isSuccessful){
            val accountResponse = api.getAccount(pair.accountId)
            if (accountResponse != null) {
                insert(Account(0L, pair.accountId, String(pair.secretSeed), accountResponse.balances[0].balance))
            }
        }

        return response
    }
}
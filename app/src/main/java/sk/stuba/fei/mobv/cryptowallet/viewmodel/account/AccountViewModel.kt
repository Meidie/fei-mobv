package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import java.net.URL
import java.util.*

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {


    init {

    }

    fun update(account: Account) = viewModelScope.launch {
        repository.update(account)
    }

    fun insert(pin: String, pair: KeyPair) = viewModelScope.launch(Dispatchers.IO) {
        val friendbotUrl = java.lang.String.format(
            "https://friendbot.stellar.org/?addr=%s",
            pair.accountId
        )
        val server = Server("https://horizon-testnet.stellar.org")
        val accountResponse: AccountResponse = server.accounts().account(pair.accountId)
        val account = Account(0, pair.accountId, pin + pair.secretSeed, accountResponse.balances.get(0).getBalance())
        repository.insert(account)
    }

    fun delete(account: Account) = viewModelScope.launch {
        repository.delete(account)
    }

}
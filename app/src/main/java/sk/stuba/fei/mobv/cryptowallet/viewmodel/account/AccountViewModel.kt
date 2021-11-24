package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import retrofit2.Response
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance
import sk.stuba.fei.mobv.cryptowallet.database.entity.Pin
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.security.Crypto

class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val _accountRegistrationResponse: MutableLiveData<Response<Void>> = MutableLiveData()
    val accountRegistrationResponse: LiveData<Response<Void>>
        get() = _accountRegistrationResponse

    init {

    }

    fun update(account: Account) = viewModelScope.launch {
        accountRepository.update(account)
    }

    fun insert(account: Account) = viewModelScope.launch {
        accountRepository.insert(account)
    }

    fun delete(account: Account) = viewModelScope.launch {
        accountRepository.delete(account)
    }

    // TODO pridat progress bar v UI lebo to trva nejaku dobu
    fun createAccount(pin: String) = viewModelScope.launch(Dispatchers.IO) {

        val pair: KeyPair = KeyPair.random()
        val response: Response<Void> = accountRepository.createAccount(pair.accountId)

        if (response.isSuccessful) {

            val accountResponse = accountRepository.getAccountInfo(pair.accountId)
            if (accountResponse != null) {

                val crypto = Crypto()
                val cipherData = crypto.encrypt(String(pair.secretSeed), pin)
                val hashedPin = crypto.secretKeyGenerator.generateSecretKey(pin)

                val accId = accountRepository.insert(
                    Account(
                        0L, pair.accountId, cipherData,
                        Pin(hashedPin.salt, hashedPin.secretKey.encoded),
                        true
                    )
                )

                for (balance in accountResponse.balances) {
                    balanceRepository.insert(Balance(0L, accId, balance.assetType, balance.balance))
                }
            }
        }

        _accountRegistrationResponse.postValue(response)
    }
}
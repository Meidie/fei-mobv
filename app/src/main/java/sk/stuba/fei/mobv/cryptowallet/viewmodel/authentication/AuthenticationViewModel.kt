package sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import retrofit2.Response
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance
import sk.stuba.fei.mobv.cryptowallet.database.entity.PinData
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import sk.stuba.fei.mobv.cryptowallet.util.FormError
import sk.stuba.fei.mobv.cryptowallet.util.OneTimeEvent


class AuthenticationViewModel(
    private val accountRepository: AccountRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val doesActiveAccountExist : LiveData<Boolean> = accountRepository.doesActiveAccountsExist()

    val pinError = ObservableField<FormError>()
    val keyError = ObservableField<FormError>()

    val publicKey = ObservableField<String>()
    val privateKey = ObservableField<String>()
    val pin = ObservableField<String>()

    private val _loginSuccessful: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val loginSuccessful: LiveData<OneTimeEvent>
        get() = _loginSuccessful

    lateinit var keypair: KeyPair

    private val _loadingResponse: MutableLiveData<String> = MutableLiveData()
    val loadingResponse: LiveData<String>
        get() = _loadingResponse

    private val _accountRegistrationResponse: MutableLiveData<Response<Void>> = MutableLiveData()
    val accountRegistrationResponse: LiveData<Response<Void>>
        get() = _accountRegistrationResponse

    private val _account: MutableLiveData<Account?> = MutableLiveData()
    val account: LiveData<Account?>
        get() = _account

    init {
        pinError.set(FormError.NO_ERROR)
        keyError.set(FormError.NO_ERROR)
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

    fun loginActive(): Job = viewModelScope.launch {

        val pin = pin.get()
        when {
            pin.isNullOrEmpty() -> pinError.set(FormError.MISSING_VALUE)
            pin.length != 4 -> pinError.set(FormError.INVALID_PIN)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val exist = accountRepository.doesActiveAccountsExistAsync()

                    if (exist) {
                        val activeAccount = accountRepository.getActiveAccount()
                        val hashedPin = Crypto().secretKeyGenerator.generateSecretKey(
                            pin, activeAccount.pinData.salt
                        ).encoded

                        if (hashedPin.contentEquals(activeAccount.pinData.pin)) {
                            _loginSuccessful.postValue(OneTimeEvent())
                        } else {
                            pinError.set(FormError.INVALID_PIN)
                        }
                    } else {
                        keyError.set(FormError.ACCOUNT_NOT_FOUND)
                    }
                }
            }
        }
    }

    fun login() {

        val key = privateKey.get()
        when {
            key.isNullOrEmpty() -> keyError.set(FormError.MISSING_VALUE)
            key.length != 56 -> keyError.set(FormError.INVALID_PK_LENGTH)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val pair = KeyPair.fromSecretSeed(key)
                    val account: Account? = accountRepository.findByPublicKey(pair.accountId)

                    if (account != null) {
                        account.active = true
                        accountRepository.update(account)
                        _loginSuccessful.postValue(OneTimeEvent())
                    } else {
                        keyError.set(FormError.ACCOUNT_NOT_FOUND)
                    }
                }
            }
        }
    }

    // Register
    fun createAccount() {

        val pin = pin.get()
        when {
            pin.isNullOrEmpty() -> pinError.set(FormError.MISSING_VALUE)
            pin.length != 4 -> pinError.set(FormError.INVALID_PIN)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _loadingResponse.postValue("loading")
                    val pair: KeyPair = KeyPair.random()
                    val response: Response<Void> = accountRepository.createAccount(pair.accountId)
                    keypair = pair
                    if (response.isSuccessful) {
                        insertAccount(pair, pin)
                    }
                    _accountRegistrationResponse.postValue(response)
                }
            }
        }
    }

    // import
    fun importAccount() {

        val pin = pin.get()
        val privateKey = privateKey.get()

        when {
            pin.isNullOrEmpty() -> {
                pinError.set(FormError.MISSING_VALUE)
            }
            pin.length != 4 -> {
                pinError.set(FormError.INVALID_PK_LENGTH)
            }
            else -> {
                val pair = KeyPair.fromSecretSeed(privateKey)

                viewModelScope.launch(Dispatchers.IO) {
                    _loadingResponse.postValue("loading")
                    val acc = accountRepository.findByPublicKey(pair.accountId)
                    if(acc == null){
                        insertAccount(pair, pin)
                        _loginSuccessful.postValue(OneTimeEvent())
                    } else {
                        keyError.set(FormError.SK_ALREADY_EXISTS)
                    }
                }
            }
        }
    }

    // helper method

    private suspend fun insertAccount(pair: KeyPair, pin: String) {
        val accountResponse = accountRepository.getAccountInfo(pair.accountId)
        if (accountResponse != null) {

            val crypto = Crypto()
            val cipherData = crypto.encrypt(String(pair.secretSeed), pin)
            val hashedPin = crypto.secretKeyGenerator.generateSecretKey(pin)

            val accId = accountRepository.insert(
                Account(
                    0L, pair.accountId, cipherData,
                    PinData(hashedPin.salt, hashedPin.secretKey.encoded),
                    true
                )
            )

            for (balance in accountResponse.balances) {
                balanceRepository.insert(Balance(0L, accId, balance.assetType, balance.balance))
            }
        }
    }


}


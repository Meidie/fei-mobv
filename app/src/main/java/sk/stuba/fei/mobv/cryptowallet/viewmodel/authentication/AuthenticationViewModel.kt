package sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication

import android.text.Editable
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

    enum class LoginState{
        SUCCESSFUL,
        FAILURE
    }

    val doesActiveAccountExist : LiveData<Boolean> = accountRepository.doesActiveAccountsExist()

    val keyError = ObservableField<FormError>()
    val publicKey = ObservableField<String>()
    val privateKey = ObservableField<String>()
    val pin = ObservableField<String>()

    private val _pinError: MutableLiveData<FormError> = MutableLiveData()
    val pinError: LiveData<FormError>
        get() = _pinError

    private val _loginResult: MutableLiveData<LoginState> = MutableLiveData()
    val loginResult: LiveData<LoginState>
        get() = _loginResult


    private val _signedOut: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val signedOut: LiveData<OneTimeEvent>
        get() = _signedOut

    lateinit var keypair: KeyPair

    private val _startLoading: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val startLoading: LiveData<OneTimeEvent>
        get() = _startLoading

    private val _accountRegistrationResponse: MutableLiveData<Response<Void>> = MutableLiveData()
    val accountRegistrationResponse: LiveData<Response<Void>>
        get() = _accountRegistrationResponse

    private val _account: MutableLiveData<Account?> = MutableLiveData()
    val account: LiveData<Account?>
        get() = _account

    init {
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
            pin.isNullOrEmpty() -> _pinError.postValue(FormError.REQUIRED_PIN)
            pin.length != 4 -> _pinError.postValue(FormError.INVALID_PIN_LENGTH)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val exist = accountRepository.doesActiveAccountsExistAsync()

                    if (exist) {
                        val activeAccount = accountRepository.getActiveAccount()
                        val hashedPin = Crypto().secretKeyGenerator.generateSecretKey(
                            pin, activeAccount.pinData.salt
                        ).encoded

                        if (hashedPin.contentEquals(activeAccount.pinData.pin)) {
                            _loginResult.postValue(LoginState.SUCCESSFUL)
                        } else {
                            _pinError.postValue(FormError.INVALID_PIN)
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
            !key.startsWith("S") -> keyError.set(FormError.INVALID_SK_FORMAT)
            key.length != 56 -> keyError.set(FormError.INVALID_PK_LENGTH)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {

                    runCatching {
                        _startLoading.postValue(OneTimeEvent())
                        KeyPair.fromSecretSeed(key)
                    }.onSuccess {
                        val account: Account? = accountRepository.findByPublicKey(it.accountId)

                        if (account != null) {
                            account.active = true
                            accountRepository.update(account)
                            _loginResult.postValue(LoginState.SUCCESSFUL)
                        } else {
                            _loginResult.postValue(LoginState.FAILURE)
                            keyError.set(FormError.ACCOUNT_NOT_FOUND)
                        }
                    }.onFailure {
                        _loginResult.postValue(LoginState.FAILURE)
                        keyError.set(FormError.INVALID_SK)
                    }
                }
            }
        }
    }

    // Register
    fun createAccount() {

        val pin = pin.get()
        when {
            pin.isNullOrEmpty() -> _pinError.postValue(FormError.REQUIRED_PIN)
            pin.length != 4 -> _pinError.postValue(FormError.INVALID_PIN_LENGTH)
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _startLoading.postValue(OneTimeEvent())
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
        var valid = true

        if (pin.isNullOrEmpty()) {
            _pinError.postValue(FormError.REQUIRED_PIN)
            valid = false
        } else if (pin.length != 4) {
            _pinError.postValue(FormError.INVALID_PIN_LENGTH)
            valid = false
        }

        if (privateKey.isNullOrEmpty()) {
            keyError.set(FormError.MISSING_VALUE)
            valid = false
        } else if (!privateKey.startsWith("S")) {
            _pinError.postValue(FormError.INVALID_PIN_LENGTH)
            valid = false
        } else if (privateKey.length != 56) {
            keyError.set(FormError.INVALID_PK_LENGTH)
            valid = false
        }

        if (valid) {
            runCatching {
                _startLoading.postValue(OneTimeEvent())
                KeyPair.fromSecretSeed(privateKey)
            }.onSuccess { kp ->
                viewModelScope.launch(Dispatchers.IO) {
                    pin?.let {
                        val acc = accountRepository.findByPublicKey(kp.accountId)
                        if (acc == null) {
                            insertAccount(kp, pin)
                            _loginResult.postValue(LoginState.SUCCESSFUL)
                        } else {
                            _loginResult.postValue(LoginState.FAILURE)
                            keyError.set(FormError.SK_ALREADY_EXISTS)
                        }
                    }
                }
            }.onFailure {
                _loginResult.postValue(LoginState.FAILURE)
                keyError.set(FormError.INVALID_SK)
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        accountRepository.signOut()
        _signedOut.postValue(OneTimeEvent())
    }

    // event listeners

    fun onPrivateKeyChanged(key: Editable?) {
        keyError.set(FormError.NO_ERROR)

        if (key.toString().isNotEmpty()) {
            if (!key.toString().startsWith("S")) {
                keyError.set(FormError.INVALID_SK_FORMAT)
            } else if (key.toString().length > 56) {
                keyError.set(FormError.INVALID_PK_LENGTH)
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


package sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction

import android.text.Editable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.responses.AccountResponse
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.*
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import sk.stuba.fei.mobv.cryptowallet.util.FormError
import sk.stuba.fei.mobv.cryptowallet.util.OneTimeEvent

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    contactRepository: ContactRepository) : ViewModel() {

    val keyError = ObservableField<FormError>()
    val amountError = ObservableField<FormError>()
    val pinError = ObservableField<FormError>()

    val allTransactionAndContact: LiveData<List<TransactionAndContact>> =
        transactionRepository.getAllTransactionAndContact()
    val allTransactionWithoutContact: LiveData<List<Transaction>> =
        transactionRepository.getAllTransactionWithoutContact()
    val contactList: LiveData<List<Contact>> = contactRepository.getAllContacts()

    val publicKey = ObservableField<String>()
    val amount = ObservableField<String>()
    val pin = ObservableField<String>()

    private var selectedContact : Contact? = null
    private var  stellarAccount: AccountResponse? = null
    lateinit var activeAccount: Account

    private val _progressBarVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBarVisible: LiveData<Boolean>
        get() = _progressBarVisible

    private val _transactionsSynced: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val transactionsSynced: LiveData<OneTimeEvent>
        get() = _transactionsSynced

    private val _clearSelect: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val clearSelect: LiveData<OneTimeEvent>
        get() = _clearSelect

    private val _transactionSentAction = MutableLiveData<Triple<Boolean, String, Int>>()
    val transactionSentAction: LiveData<Triple<Boolean, String, Int>>
        get() = _transactionSentAction

    init {
        keyError.set(FormError.NO_ERROR)
        amountError.set(FormError.NO_ERROR)
        pinError.set(FormError.NO_ERROR)
        getAccount()
    }

    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.insert(transaction)
    }

    // TODO doplniť možnosť zadať maximálne current balance (asi by bolo fajn aj niekde zobrazit aktualny kapital)
    // TODO overenie existenie PK na ktory posielame
    fun sendTransaction() {

        val sAccount = stellarAccount
        val amount = amount.get()
        val pk = publicKey.get()
        val pin = pin.get()


        if(isFormValid(pk, amount, pin)){
            sAccount?.let {
                viewModelScope.launch(Dispatchers.IO) {

                    _progressBarVisible.postValue(true)
                   val response = transactionRepository.sendTransaction(activeAccount, it, amount!!.replace(",","."),  pk!!, pin!!)

                    if(response!= null && response.isSuccess){
                        _transactionSentAction.postValue(
                            Triple(true, "Transaction  sent successfully",
                                R.id.action_transactionAddFragment_to_transactionListFragment
                            )
                        )
                    } else {
                        _transactionSentAction.postValue(
                            Triple(false, "Something went wrong!", -1
                            )
                        )
                    }

                    _progressBarVisible.postValue(false)
                }
            }
        } else {
            _transactionSentAction.postValue(Triple(false, "Please fill all fields correctly", -1))
        }
    }

    fun syncTransactions() = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.syncTransactions(activeAccount)
        _transactionsSynced.postValue(OneTimeEvent())
    }

    // event listeners

    fun onItemSelected(position: Int) {

        selectedContact = contactList.value?.get(position)
        val contact = selectedContact

        if (contact != null) {
            publicKey.set(contact.publicKey)
        } else {
            publicKey.set("")
        }
    }

    fun onKeyChanged(text: Editable?) {

        keyError.set(FormError.NO_ERROR)

        if (!text.isNullOrEmpty()) {
            validateKey(text.toString())
        }

        val contact = selectedContact
        if (contact != null && text.toString() != contact.publicKey) {
            _clearSelect.postValue(OneTimeEvent())
        }
    }

    fun onAmountChanged(text: Editable?) {
        if (!text.isNullOrEmpty() && amountError.get()!! != FormError.NO_ERROR) {
            amountError.set(FormError.NO_ERROR)
        }
    }

    fun onPinChanged(text: Editable?) {
        if (!text.isNullOrEmpty() && pinError.get()!! != FormError.NO_ERROR) {
            pinError.set(FormError.NO_ERROR)
        }
    }

    // helper methods

    private fun isFormValid(key: String?, amount: String?, pin: String?): Boolean {

        when {
            key.isNullOrEmpty() -> {
                keyError.set(FormError.MISSING_VALUE)
            }
            key.length != 56 -> {
                keyError.set(FormError.INVALID_PK_LENGTH)
            }
            else -> {
                validateKey(key)
            }
        }

        if(amount.isNullOrEmpty()){
            amountError.set(FormError.MISSING_VALUE)
        } else {
            validateAmount(amount)
        }

        if(pin.isNullOrEmpty()){
            pinError.set(FormError.MISSING_VALUE)
        } else {
            validatePin(pin)
        }

        return (amountError.get()!! == FormError.NO_ERROR
                && keyError.get()!! == FormError.NO_ERROR
                && pinError.get()!! == FormError.NO_ERROR)
    }

    private fun validateKey(key: String) {
        if (!key.startsWith("G")) {
            keyError.set(FormError.INVALID_PK_FORMAT)
        } else if (key.length > 56) {
            keyError.set(FormError.INVALID_PK_LENGTH)
        }
    }

    private fun validateAmount(amount: String) {
        if (amount.startsWith("-")) {
            amountError.set(FormError.NEGATIVE_VALUE)
        } else if(amount.contains(",")) {
            amountError.set(FormError.INCORRECT_CHAR)
        }
        // TODO: zapojit max
//        else if(amount.toFloat() > 100){
//            val error = FormError.ACCOUNT_BALANCE_EXCEEDED
//            error.message += "123"
//            amountError.set(error)
//        }
    }

    private fun validatePin(pin: String) {
        if (!Crypto().secretKeyGenerator.generateSecretKey(pin, activeAccount.pinData.salt)
                .encoded.contentEquals(activeAccount.pinData.pin)
        ) {
            pinError.set(FormError.INVALID_PIN)
        }
    }

    private fun getAccount() = viewModelScope.launch(Dispatchers.IO){
        activeAccount = accountRepository.getActiveAccount()
        stellarAccount = accountRepository.getAccountInfo(activeAccount.publicKey)
    }
}
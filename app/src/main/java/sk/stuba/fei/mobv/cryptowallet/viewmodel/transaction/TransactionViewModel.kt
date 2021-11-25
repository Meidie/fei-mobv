package sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.AssetTypeNative
import org.stellar.sdk.Network
import org.stellar.sdk.PaymentOperation
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.security.Crypto

class TransactionViewModel(
    private val transactionRepository: TransactionRepository, private val api: StellarApi,
    private val accountRepository: AccountRepository) : ViewModel() {

    val allContacts: LiveData<List<Transaction>> = transactionRepository.getAllTransactions()
    lateinit var account: Account

    private val _newTransactionResponse: MutableLiveData<SubmitTransactionResponse?> = MutableLiveData()
    val newTransactionResponse: LiveData<SubmitTransactionResponse?> get() = _newTransactionResponse

    init {
        getAccount()
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.insert(transaction)
    }

    fun getAccount() = viewModelScope.launch(Dispatchers.IO){
        account = accountRepository.getActiveAccount()
    }

    fun sendTransaction(transaction: Transaction, pin: String) = viewModelScope.launch(Dispatchers.IO) {

        val sourceAccount: AccountResponse? = accountRepository.getAccountInfo(accountRepository.getActiveAccount().publicKey)

        val newTransaction: org.stellar.sdk.Transaction = org.stellar.sdk.Transaction.Builder(
            sourceAccount,
            Network.TESTNET
        )
            .addOperation(
                PaymentOperation.Builder(
//                    transaction.publicKey,
                    transaction.publicKey,
                    AssetTypeNative(),
                    transaction.amount
                ).build()
            )
            .setTimeout(180)
            .setBaseFee(org.stellar.sdk.Transaction.MIN_BASE_FEE)
            .build()

        newTransaction.sign(Crypto().decrypt(account.privateKeyData!!, pin))

        val response = api.sendTransaction(newTransaction)

        if(response != null){
            insert(transaction)
        }
        _newTransactionResponse.postValue(response)
    }
}
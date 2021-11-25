package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel
import java.lang.IllegalArgumentException

class TransactionViewModelFactory(private val transactionRepository: TransactionRepository, private val stellarApi: StellarApi,
                                  private val accountRepository: AccountRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TransactionViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository, stellarApi, accountRepository) as T
        }
        throw IllegalArgumentException("Unknown TransactionViewModel class")
    }

}
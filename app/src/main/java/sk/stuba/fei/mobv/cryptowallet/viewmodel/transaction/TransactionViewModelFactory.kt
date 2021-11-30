package sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import java.lang.IllegalArgumentException

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val balanceRepository: BalanceRepository,
    private val contactRepository: ContactRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository, accountRepository, balanceRepository, contactRepository) as T
        }
        throw IllegalArgumentException("Unknown TransactionViewModel class")
    }
}

package sk.stuba.fei.mobv.cryptowallet.viewmodel.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository

class BalanceViewModelFactory(
    private val accountRepository: AccountRepository,
    private val balanceRepository: BalanceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BalanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BalanceViewModel(accountRepository, balanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

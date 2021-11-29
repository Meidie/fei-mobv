package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository

class AccountViewModelFactory(private val accountRepository: AccountRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
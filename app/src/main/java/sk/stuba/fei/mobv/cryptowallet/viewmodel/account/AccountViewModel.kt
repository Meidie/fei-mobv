package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {


    init {

    }

    fun update(account: Account) = viewModelScope.launch {
        repository.update(account)
    }

    fun insert(account: Account) = viewModelScope.launch {
        repository.insert(account)
    }

    fun delete(account: Account) = viewModelScope.launch {
        repository.delete(account)
    }

}
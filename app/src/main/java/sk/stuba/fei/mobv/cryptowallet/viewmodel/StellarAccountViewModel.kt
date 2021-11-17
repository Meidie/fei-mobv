package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.StellarAccount
import sk.stuba.fei.mobv.cryptowallet.repository.StellarAccountRepository

class StellarAccountViewModel(private val repository: StellarAccountRepository) : ViewModel() {

    init {

    }

    fun insert(account: StellarAccount) = viewModelScope.launch {
        repository.insert(account)
    }
}
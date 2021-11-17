package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.StellarTransaction
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allContacts: LiveData<List<StellarTransaction>> = repository.getAllTransactions()

    init {

    }

    fun insert(transaction: StellarTransaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
}
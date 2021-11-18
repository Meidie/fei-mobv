package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allContacts: LiveData<List<Transaction>> = repository.getAllTransactions()

    init {

    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
}
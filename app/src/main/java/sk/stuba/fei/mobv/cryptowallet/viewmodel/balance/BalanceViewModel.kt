package sk.stuba.fei.mobv.cryptowallet.viewmodel.balance

import android.text.Editable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.util.FormError

class BalanceViewModel(
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val allBalances = balanceRepository.getAllBalances()

    fun insert(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.insert(balance)
    }

    fun update(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
       balanceRepository.update(balance)
    }

    fun delete(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.delete(balance)
    }

}

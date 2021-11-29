package sk.stuba.fei.mobv.cryptowallet.viewmodel.balance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.stellar.sdk.responses.AccountResponse
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository

class BalanceViewModel(
    private val accountRepository: AccountRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val allBalances = balanceRepository.getAllBalances()
    private var  stellarAccount: AccountResponse? = null
    lateinit var activeAccount: Account

    init {
        updateBalances()
    }

    fun insert(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.insert(balance)
    }

    fun update(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
       balanceRepository.update(balance)
    }

    fun delete(balance: Balance) = viewModelScope.launch(Dispatchers.IO) {
        balanceRepository.delete(balance)
    }

    fun updateBalances() = viewModelScope.launch(Dispatchers.IO) {
        activeAccount = accountRepository.getActiveAccount()
        stellarAccount = accountRepository.getAccountInfo(activeAccount.publicKey)
        if (stellarAccount != null) {
            for (balance in stellarAccount!!.balances) {
                balanceRepository.updateBalances(balance.balance, balance.assetType)
            }
        }
    }

}

package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.util.OneTimeEvent

class AccountViewModel( private val accountRepository: AccountRepository) : ViewModel() {

    val publicKey = ObservableField<String>()
    val privateKey = ObservableField<String>()
    lateinit var activeAccount: Account

    private val _signedOut: MutableLiveData<OneTimeEvent> = MutableLiveData()
    val signedOut: LiveData<OneTimeEvent>
        get() = _signedOut

    init {
        getAccount()
    }

    fun signOut() = viewModelScope.launch {
        accountRepository.signOut()
        _signedOut.postValue(OneTimeEvent())
    }

    private fun getAccount() = viewModelScope.launch(Dispatchers.IO){
        activeAccount = accountRepository.getActiveAccount()
        publicKey.set(activeAccount.publicKey)
        privateKey.set(activeAccount.publicKey)
    }
}
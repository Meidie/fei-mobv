package sk.stuba.fei.mobv.cryptowallet.viewmodel.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _accountRegistrationResponse: MutableLiveData<Response<Void>> = MutableLiveData()
    val accountRegistrationResponse: LiveData<Response<Void>>
        get() = _accountRegistrationResponse

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

    fun createAccount(pin: String) = viewModelScope.launch(Dispatchers.IO) {
        _accountRegistrationResponse.postValue(repository.createAccount(pin))
    }
}
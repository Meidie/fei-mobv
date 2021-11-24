package sk.stuba.fei.mobv.cryptowallet.viewmodel.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository

class ContactViewModel(
    private val contactRepository: ContactRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val allContacts: LiveData<List<Contact>> = contactRepository.getAllContacts()

    init {

    }

    fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        contactRepository.insert(contact)
    }

    fun insert(name: String, key: String) = viewModelScope.launch(Dispatchers.IO) {
        val activeAccount = accountRepository.getActiveAccount()
        contactRepository.insert(Contact(0, activeAccount.accountId, name, key))
    }

    fun update(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        contactRepository.update(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        contactRepository.delete(contact)
    }

    fun deleteAllContacts() = viewModelScope.launch(Dispatchers.IO) {
        contactRepository.deleteAll()
    }
}

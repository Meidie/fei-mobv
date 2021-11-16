package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    val allContacts: LiveData<List<Contact>> = repository.getAllContacts()

    init {

    }

    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }
}

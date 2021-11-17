package sk.stuba.fei.mobv.cryptowallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.model.enum.Test
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    val allContacts: LiveData<List<Contact>> = repository.getAllContacts()

    init {

    }

    // TODO - delete
    fun test(){
        viewModelScope.launch(Dispatchers.IO) {
            Test().getAccount("GBOWAUCRCZPPI5WE6RAMRNHILHVZAI6SHIFTXRKDECIKK4273H755I37")
        }
    }

    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }
}

package sk.stuba.fei.mobv.cryptowallet.viewmodel.contact

import android.text.Editable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.util.FormError

class ContactViewModel(
    private val contactRepository: ContactRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val nameError = ObservableField<FormError>()
    val keyError = ObservableField<FormError>()

    private val _contactSaveAction = MutableLiveData<Triple<Boolean, String, Int>>()
    val contactSaveAction: LiveData<Triple<Boolean, String, Int>>
        get() = _contactSaveAction

    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact>
        get() = _contact

    val allContacts: LiveData<List<Contact>> = contactRepository.getAllContacts()

    init {
        nameError.set(FormError.NO_ERROR)
        keyError.set(FormError.NO_ERROR)
        _contact.postValue(Contact())
    }

    fun setContact(contact: Contact) {
        _contact.postValue(contact)
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

    fun signOut() = viewModelScope.launch {
        accountRepository.signOut()
    }

    fun insertContactToDatabase() {

        val insertContact = contact.value

        if (insertContact != null && isFormValid(insertContact.name, insertContact.publicKey)) {
            viewModelScope.launch(Dispatchers.IO) {
                val activeAccount = accountRepository.getActiveAccount()
                contactRepository.insert(
                    Contact(
                        0, activeAccount.accountId,
                        insertContact.name, insertContact.publicKey
                    )
                )
                _contactSaveAction.postValue(
                    Triple(true, "Contact successfully added",
                        R.id.action_contactAddFragment_to_contactListFragment
                    )
                )
            }
        } else {
            _contactSaveAction.postValue(Triple(false, "Please fill all fields correctly", -1))
        }
    }

    fun updateContactInDatabase() {

        val editContact = contact.value

        if (editContact != null && isFormValid(editContact.name, editContact.publicKey)) {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedContact = Contact(
                    editContact.contactId,
                    editContact.accountOwnerId,
                    editContact.name,
                    editContact.publicKey
                )
                contactRepository.update(updatedContact)
                _contactSaveAction.postValue(
                    Triple(true,"Contact successfully updated",
                        R.id.action_contactEditFragment_to_contactListFragment)
                )
            }
        } else {
            _contactSaveAction.postValue(Triple(false, "Please fill all fields correctly", -1))
        }
    }

    // event listeners

    fun onKeyChanged(text: Editable?) {

        keyError.set(FormError.NO_ERROR)

        if (!text.isNullOrEmpty()) {
            validateKey(text.toString())
        }
    }

    fun onNameChanged(text: Editable?) {
        if (!text.isNullOrEmpty() && nameError.get()!! != FormError.NO_ERROR) {
            nameError.set(FormError.NO_ERROR)
        }
    }
    // helper methods

    private fun isFormValid(name: String?, key: String?): Boolean {

        if (name.isNullOrEmpty()) {
            nameError.set(FormError.MISSING_VALUE)
        }

        when {
            key.isNullOrEmpty() -> {
                keyError.set(FormError.MISSING_VALUE)
            }
            key.length != 56 -> {
                keyError.set(FormError.INVALID_PK_LENGTH)
            }
            else -> {
                validateKey(key)
            }
        }

        return (nameError.get()!! == FormError.NO_ERROR && keyError.get()!! == FormError.NO_ERROR)
    }

    private fun validateKey(key: String) {
        if (!key.startsWith("G")) {
            keyError.set(FormError.INVALID_PK_FORMAT)
        } else if (key.length > 56) {
            keyError.set(FormError.INVALID_PK_LENGTH)
        }
    }
}

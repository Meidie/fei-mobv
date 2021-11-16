package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAddContactBinding
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModelFactory

class AddContactFragment : Fragment() {

    private lateinit var contactViewModel: ContactViewModel
    private  var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(ContactRepository(database.contactDao()))
        ).get(ContactViewModel::class.java)

        binding.addButton.setOnClickListener {
            insertContactToDatabase()
        }

        return binding.root
    }

    private fun insertContactToDatabase() {
        val firstName = binding.addFirstName.text.toString()
        val lastName = binding.addLastname.text.toString()
        val key = binding.addKey.text.toString()

        if (areInputsValid(firstName, lastName, key)) {
            val newContact = Contact(0, firstName, lastName, key)
            contactViewModel.insert(newContact)
            Toast.makeText(requireContext(), "Contact successfully added", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.action_addContactFragment_to_contactListFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun areInputsValid(firstName: String, lastName: String, key: String): Boolean {
        return !(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(
            key
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
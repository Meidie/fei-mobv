package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactAddBinding
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory

class ContactAddFragment : Fragment() {

    private lateinit var contactViewModel: ContactViewModel
    private var _binding: FragmentContactAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactAddBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(ContactRepository(database.contactDao()))
        ).get(ContactViewModel::class.java)

        binding.addButton.setOnClickListener {
            insertContactToDatabase()
        }

        binding.addKeyText.doOnTextChanged { text, _, _, _ ->
            if (!text!!.startsWith("G")) {
                binding.addKeyLayout.error = "Key must start with \'G\' character"
            } else if (text.length > 56) {
                binding.addKeyLayout.error = "Key must contain 56 alphanumeric characters"
            } else {
                binding.addKeyLayout.error = null
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun insertContactToDatabase() {
        val firstName = binding.addNameText.text.toString()
        val key = binding.addKeyText.text.toString()

        if (areInputsValid(firstName, key)) {
            val newContact = Contact(0, firstName, key)
            contactViewModel.insert(newContact)
            Toast.makeText(requireContext(), "Contact successfully added", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.action_contactAddFragment_to_contactListFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG)
                .show()
        }
    }


    // helper methods

    private fun areInputsValid(name: String, key: String): Boolean {

        var valid = true

        if (TextUtils.isEmpty(name)) {
            valid = false
            binding.addNameLayout.error = "Value is required!"
        }

        if (TextUtils.isEmpty(key)) {
            valid = false
            binding.addKeyLayout.error = "Value is required!"
        }

        return valid
    }
}
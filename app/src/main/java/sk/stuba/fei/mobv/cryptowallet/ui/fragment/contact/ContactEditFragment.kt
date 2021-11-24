package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactEditBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory

class ContactEditFragment : Fragment() {

    private val args: ContactEditFragmentArgs by navArgs()
    private lateinit var contact: Contact

    private lateinit var contactViewModel: ContactViewModel
    private var _binding: FragmentContactEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContactEditBinding.inflate(inflater, container, false)
        contact = args.currentContact

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(
                ContactRepository(database.contactDao()),
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[ContactViewModel::class.java]

        setDataFromArgs()

        binding.editButton.setOnClickListener {
            updateContactInDatabase()
        }

        binding.editKeyText.doOnTextChanged { text, _, _, _ ->
            if (!text!!.startsWith("G")) {
                binding.editKeyLayout.error = "Key must start with \'G\' character"
            } else if (text.length > 56) {
                binding.editKeyLayout.error = "Key must contain 56 alphanumeric characters"
            } else {
                binding.editKeyLayout.error = null
            }
        }

        // Add menu
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_menu) {
            deleteContacts()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateContactInDatabase() {
        val name = binding.editNameText.text.toString()
        val key = binding.editKeyText.text.toString()

        if (areInputsValid(name, key)) {
            val updatedContact = Contact(contact.contactId, contact.accountOwnerId, name, key)
            contactViewModel.update(updatedContact)
            Toast.makeText(requireContext(), "Contact successfully updated", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.action_contactEditFragment_to_contactListFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun deleteContacts() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            contactViewModel.delete(contact)
            Toast.makeText(
                requireContext(), "Successfully removed: ${contact.name}",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(R.id.action_contactEditFragment_to_contactListFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${contact.name}")
        builder.setMessage("Are you sure you want to delete ${contact.name}?")
        builder.create().show()
    }

    // helper methods

    private fun setDataFromArgs() {
        binding.editNameText.setText(contact.name)
        binding.editKeyText.setText(contact.publicKey)
    }

    private fun areInputsValid(name: String, key: String): Boolean {

        var valid = true

        if (TextUtils.isEmpty(name)) {
            valid = false
            binding.editNameLayout.error = "Value is required!"
        }

        if (TextUtils.isEmpty(key)) {
            valid = false
            binding.editKeyLayout.error = "Value is required!"
        }

        return valid
    }
}
package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentEditContactBinding
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModelFactory

class EditContactFragment : Fragment() {

    private val args: EditContactFragmentArgs by navArgs()
    private lateinit var contact: Contact

    private lateinit var contactViewModel: ContactViewModel
    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditContactBinding.inflate(inflater, container, false)
        contact = args.currentContact

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(ContactRepository(database.contactDao()))
        ).get(ContactViewModel::class.java)

        setDataFromArgs()

        binding.editButton.setOnClickListener {
            updateContactInDatabase()
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
        val firstName = binding.editFirstName.text.toString()
        val lastName = binding.editLastname.text.toString()
        val key = binding.editKey.text.toString()

        if (areInputsValid(firstName, lastName, key)) {
            val updatedContact = Contact(contact.contactId, firstName, lastName, key)
            contactViewModel.update(updatedContact)
            Toast.makeText(requireContext(), "Contact successfully updated", Toast.LENGTH_SHORT)
                .show()

            findNavController().navigate(R.id.action_editContactFragment_to_contactListFragment)
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
                requireContext(), "Successfully removed: ${contact.firstName}",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(R.id.action_editContactFragment_to_contactListFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${contact.firstName}")
        builder.setMessage("Are you sure you want to delete ${contact.firstName}?")
        builder.create().show()
    }

    // helper methods

    private fun setDataFromArgs() {
        binding.editFirstName.setText(contact.firstName)
        binding.editLastname.setText(contact.lastName)
        binding.editKey.setText(contact.publicKey)
    }

    private fun areInputsValid(firstName: String, lastName: String, key: String): Boolean {
        return !(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(
            key
        ))
    }
}
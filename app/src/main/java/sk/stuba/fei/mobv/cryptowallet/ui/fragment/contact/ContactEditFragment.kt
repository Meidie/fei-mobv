package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
    private lateinit var contactArgs: Contact

    private lateinit var contactViewModel: ContactViewModel
    private var _binding: FragmentContactEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContactEditBinding.inflate(inflater, container, false)
        contactArgs = args.currentContact

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(
                ContactRepository(database.contactDao()),
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[ContactViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = contactViewModel

        setDataFromArgs()

        binding.editKeyLayout.setEndIconOnClickListener {

            val pk =  contactViewModel.contact.value?.publicKey
            if(!pk.isNullOrEmpty()){
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("public key", pk)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), "Public key copied to clipboard", Toast.LENGTH_LONG).show()
            }
        }

        contactViewModel.contactSaveAction.observe(viewLifecycleOwner, {
            it?.let {
                if (it.first) {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(it.third)
                } else {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_LONG).show()
                }
            }
        })

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

    private fun deleteContacts() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            contactViewModel.delete(contactArgs)
            Toast.makeText(
                requireContext(), "Successfully removed: ${contactArgs.name}",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(R.id.action_contactEditFragment_to_contactListFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${contactArgs.name}")
        builder.setMessage("Are you sure you want to delete ${contactArgs.name}?")
        builder.create().show()
    }

    // helper methods

    private fun setDataFromArgs() {
        contactViewModel.setContact(
            Contact(
                contactArgs.contactId,
                contactArgs.accountOwnerId,
                contactArgs.name,
                contactArgs.publicKey
            )
        )
    }
}
package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.ContactRowBinding
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.ContactListAdapter
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.ContactViewModelFactory

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       // ContactRowBinding

        _binding = FragmentContactListBinding.inflate(inflater, container, false)

        // ViewModel
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(ContactRepository(database.contactDao()))
        ).get(ContactViewModel::class.java)

        // RecyclerView
        val adapter = ContactListAdapter()
        binding.contactListRecycleView.adapter = adapter
        contactViewModel.allContacts.observe(viewLifecycleOwner, {
            it?.let {
                adapter.contactList = it
            }
        })

        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_contactListFragment_to_addContactFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
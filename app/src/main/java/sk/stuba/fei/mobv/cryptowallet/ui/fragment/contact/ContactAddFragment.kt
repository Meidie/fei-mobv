package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactAddBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
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
            ContactViewModelFactory(
                ContactRepository(database.contactDao()),
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[ContactViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = contactViewModel

        contactViewModel.contactSaveAction.observe(viewLifecycleOwner, {
            it?.let {
                if (it.first) {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_contactAddFragment_to_contactListFragment)
                } else {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_LONG).show()
                }
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
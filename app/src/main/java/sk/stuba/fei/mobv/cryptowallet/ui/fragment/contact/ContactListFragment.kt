package sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.contact.ContactListAdapter
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactViewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContactListBinding.inflate(inflater, container, false)

        // ViewModel
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(
                ContactRepository(database.contactDao()),
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[ContactViewModel::class.java]

        // RecyclerView
        val adapter = ContactListAdapter()
        binding.contactListRecycleView.adapter = adapter
        binding.contactListRecycleView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        contactViewModel.allContacts.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isEmpty()) {
                    binding.contactListRecycleView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.contactListRecycleView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }

                adapter.submitList(it.sortedBy { c -> c.name })
            }
        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_contactListFragment_to_contactAddFragment)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_menu) {
            deleteAllContacts()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun deleteAllContacts() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            contactViewModel.deleteAllContacts()
            Toast.makeText(
                requireContext(), "Everything removes successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete all contacts?")
        builder.create().show()
    }
}
package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionAndContact
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.TransactionListAdapter
import sk.stuba.fei.mobv.cryptowallet.util.visible
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionListFragment : Fragment(R.layout.fragment_transaction_list) {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(
                TransactionRepository(database.transactionDao(), StellarApi()),
                AccountRepository(database.accountDao(), StellarApi()),
                ContactRepository(database.contactDao())
            )
        )[TransactionViewModel::class.java]

        val adapter = TransactionListAdapter()
        binding.transactionListRecycleView.adapter = adapter
        binding.transactionListRecycleView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        // RecyclerView
        transactionViewModel.allTransactionAndContact.observe(viewLifecycleOwner, { transactionAndContact ->
            transactionViewModel.allTransactionWithoutContact.observe(viewLifecycleOwner, { transactionWithoutContact ->

                if (transactionAndContact.isEmpty() && transactionWithoutContact.isEmpty()) {
                    binding.transactionListRecycleView.visible(false)
                    binding.emptyView.visible(true)
                } else {
                    binding.transactionListRecycleView.visible(true)
                    binding.emptyView.visible(false)
                }

                val transactions: MutableList<TransactionAndContact> = mutableListOf()
                transactions.addAll(transactionAndContact)

                for (transaction in transactionWithoutContact) {
                    transactions.add(
                        TransactionAndContact(transaction,
                            Contact(0L, transaction.accountOwnerId, "", transaction.publicKey)
                        )
                    )
                }
                adapter.submitList(transactions.sortedBy { t ->
                    LocalDateTime.parse(t.transaction.dateTime, DateTimeFormatter.ISO_DATE_TIME)
                }.reversed())
            })
        })

        // transactions sync
        transactionViewModel.transactionsSynced.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })

        binding.refreshLayout.setOnRefreshListener {
             transactionViewModel.syncTransactions()
        }

        binding.addTransactionButton.setOnClickListener {
            val action = TransactionListFragmentDirections.actionTransactionListFragmentToAddTransactionFragment()
            findNavController().navigate(action)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sync_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync_menu -> {
                binding.refreshLayout.isRefreshing = true
                transactionViewModel.syncTransactions()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
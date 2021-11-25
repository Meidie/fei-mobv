package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.TransactionListAdapter
import sk.stuba.fei.mobv.cryptowallet.viewmodel.TransactionViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel

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
            TransactionViewModelFactory(TransactionRepository(database.transactionDao()), StellarApi(), AccountRepository(database.accountDao(), StellarApi()))
        )[TransactionViewModel::class.java]

        val adapter = TransactionListAdapter()
        binding.transactionListRecycleView.adapter = adapter
        binding.transactionListRecycleView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        transactionViewModel.allContacts.observe(viewLifecycleOwner, {
            it?.let {

                if (it.isEmpty()) {
                    binding.transactionListRecycleView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.transactionListRecycleView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }

                adapter.submitList(it.sortedBy { t -> t.transactionId })
            }
        })

        binding.addTransactionButton.setOnClickListener {
            val action = TransactionListFragmentDirections.actionTransactionListFragmentToAddTransactionFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
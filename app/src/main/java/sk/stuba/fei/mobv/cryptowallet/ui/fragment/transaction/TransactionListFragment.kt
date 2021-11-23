package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionListBinding
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.TransactionListAdapter
import sk.stuba.fei.mobv.cryptowallet.viewmodel.TransactionViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel

class TransactionListFragment: Fragment(R.layout.fragment_transaction_list) {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(TransactionRepository(database.transactionDao()))
        ).get(TransactionViewModel::class.java)

        val adapter = TransactionListAdapter()
        binding.transactionListRecycleView.adapter = adapter
        transactionViewModel.allContacts.observe(viewLifecycleOwner, {
            it?.let {
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
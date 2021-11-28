package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionAddBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.util.visible
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModelFactory

class TransactionAddFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private var _binding: FragmentTransactionAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionAddBinding.inflate(inflater, container, false)

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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = transactionViewModel

        // contact select clear
        transactionViewModel.clearSelect.observe(viewLifecycleOwner, {
            binding.contactSelect.setText("")
        })

        // contacts for select
        transactionViewModel.contactList.observe(viewLifecycleOwner, {

            binding.contactSelect.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item,
                    it.map { c -> c.name }.toList()
                )
            )
        })

        // progressBar visibility
        transactionViewModel.progressBarVisible.observe(viewLifecycleOwner, {
            binding.progressBar.visible(it)
        })

        // transaction Sent Action
        transactionViewModel.transactionSentAction.observe(viewLifecycleOwner, {
            it?.let {

                if (it.first) {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(it.third)
                } else {
                    Toast.makeText(requireContext(), it.second, Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.contactSelect.setOnItemClickListener { _, _, position, _ ->
           transactionViewModel.onItemSelected(position)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
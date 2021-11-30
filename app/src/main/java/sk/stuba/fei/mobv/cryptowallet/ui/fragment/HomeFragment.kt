package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentHomeBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.ui.adapter.HomeAdapter
import sk.stuba.fei.mobv.cryptowallet.util.visible
import sk.stuba.fei.mobv.cryptowallet.viewmodel.balance.BalanceViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.balance.BalanceViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var balanceViewModel: BalanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        balanceViewModel = ViewModelProvider(
            this,
            BalanceViewModelFactory(AccountRepository(database.accountDao(), StellarApi()),
                BalanceRepository(database.balanceDao()))
        )[BalanceViewModel::class.java]

        // balances sync
        balanceViewModel.balancesSynced.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
        })

        binding.refreshLayout.setOnRefreshListener {
            balanceViewModel.updateBalances()
        }

        setHasOptionsMenu(true)
        balanceViewModel.updateBalances()

        val adapter = HomeAdapter()
        binding.recyclerViewer.adapter = adapter
        balanceViewModel.allBalances.observe(viewLifecycleOwner, {
            it?.let {

                if (it.isEmpty()) {
                    binding.recyclerViewer.visible(false)
                    binding.emptyView.visible(true)
                } else {
                    binding.recyclerViewer.visible(true)
                    binding.emptyView.visible(false)
                }

                adapter.submitList(it.sortedBy { t -> t.balanceId })
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sync_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync_menu -> {
                binding.refreshLayout.isRefreshing = true
                balanceViewModel.updateBalances()
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
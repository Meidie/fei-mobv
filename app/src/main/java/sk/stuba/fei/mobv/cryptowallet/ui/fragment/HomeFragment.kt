package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentHomeBinding
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.ui.home.adapter.HomeAdapter
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
            BalanceViewModelFactory(
                BalanceRepository(database.balanceDao()))
        )[BalanceViewModel::class.java]

        val adapter = HomeAdapter()
        binding.recyclerViewer.adapter = adapter
        balanceViewModel.allBalances.observe(viewLifecycleOwner, {
            it?.let {

                if (it.isEmpty()) {
                    binding.recyclerViewer.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewer.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }

                adapter.submitList(it.sortedBy { t -> t.balanceId })
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}